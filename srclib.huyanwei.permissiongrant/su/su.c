/*
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <sys/select.h>
#include <sys/time.h>
#include <unistd.h>
#include <limits.h>
#include <fcntl.h>
#include <errno.h>
#include <endian.h>
#include <dirent.h>
#include <sys/stat.h>
#include <time.h>
#include <pwd.h>

#ifdef __cplusplus
extern "C" {
#endif

//#include <sqlite3.h>
#include "sqlite3.h"

extern int send_request_intent(const char *socket_path, int uid, int pid);
extern int send_response_intent(int grant_result, int uid, int pid);

#ifdef __cplusplus
}
#endif


#define DATA_PATH	"/data/data/srclib.huyanwei.permissiongrant/"
#define DB_PATH    	DATA_PATH"databases/permissiongrant.sqlite"
#define SOCKET_PATH	DATA_PATH
#define SOCKET_FILE	DATA_PATH".socket.srclib.XXXXXX"

//#define LOG

#if defined(LOG)
#define DEBUG(fmt,...) printf(fmt,##__VA_ARGS__);
#else
#define DEBUG(fmt,...) do {} while (0)
#endif

#define INTENT_BOARDCAST_INTER

static int 	g_puid = -1;
static int 	g_ppid = -1;
static char    *socket_path = NULL;
static int 	socket_serv_fd = -1;
static char    **arg_array = NULL;
static int    	arg_array_len = 0;

static int 	socket_create_and_listen(void);
static int 	socket_accept(int serv_fd);
static int 	socket_receive_result(int serv_fd, char *result, ssize_t result_len);
static void 	socket_cleanup(void);
static void 	cleanup(void);
static void 	cleanup_signal(int sig);
static int 	grant_request(void);
static int 	grant_pass(void);
static int 	grant_fail(void);
static int 	grant_timeout(void);
static void 	usage(void);


/************************************************************************
** socket start
*************************************************************************/
static int socket_create_and_listen(void)
{
    static char buf[PATH_MAX];
    int fd;

    struct sockaddr_un sun;

    //huyanwei add mode
    umask(0011); // default create file or dir mode

    fd = socket(AF_LOCAL, SOCK_STREAM, 0);
    if (fd < 0)
    {
        DEBUG("socket. \r\n");
        return -1;
    }

    while(1)
    {
        memset(&sun, 0, sizeof(sun));
        sun.sun_family = AF_LOCAL;
        strcpy(buf, SOCKET_FILE);
        socket_path = mktemp(buf); // make sure uniq file name . XXXXXX transfer to real file name .	
        snprintf(sun.sun_path, sizeof(sun.sun_path), "%s", socket_path);

	unlink(socket_path); // in case of file exist .

        if (bind(fd, (struct sockaddr*)&sun, sizeof(sun)) < 0)
	{
            if (errno != EADDRINUSE)
	    {
                DEBUG("bind. \r\n");
                return -2;
	    }
        }
	else 
	{
            break;
        }
    }

    
    //huyanwei add it for permission grant;
    if(socket_path != NULL)
    {
	    //chown(SOCKET_PATH,  1000, 1000);
	    chmod(socket_path,  0666);
    }

    // notify  kernel : I'm a server .
    if (listen(fd, 1) < 0) 
    {
        DEBUG("listen. \r\n");
        return -3;
    }

    return fd;
}

static int socket_accept(int serv_fd)
{
    struct timeval tv;
    fd_set fds ;
    int fd;

    /* Wait 40 seconds for a connection, then give up. */
    tv.tv_sec = 30;
    tv.tv_usec = 0;
    FD_ZERO(&fds);
    FD_SET(serv_fd, &fds);
    if (select(serv_fd + 1, &fds, NULL, NULL, &tv) < 1)
    {
        DEBUG("select .\r\n");
        return -1;
    }

    fd = accept(serv_fd, NULL, NULL);
    if (fd < 0) {
        DEBUG("accept.\r\n");
        return -1;
    }

    return fd;
}


static int socket_receive_result(int serv_fd, char *result, ssize_t result_len)
{
    ssize_t len = -1 ;
    
    while(1)
    {
        int fd = socket_accept(serv_fd);
        if (fd < 0)
	{
	    DEBUG("accept fd < 0 \r\n");
            return -1;
	}

        len = read(fd, result, result_len-1);
        if (len < 0)
	{
            DEBUG("read data err.\r\n");
            return -1;
        }

        if (len > 0)
	{
            break;
        }
    }

    result[len] = '\0';

    return 0;
}

static void socket_cleanup(void)
{
    unlink(socket_path);
	
    if( socket_serv_fd > 0 )
    {
	   close(socket_serv_fd);
	   socket_serv_fd = -1;
	}

    if(arg_array != NULL)
    {
	free(arg_array);
	arg_array = NULL;
    }
}

static void cleanup(void)
{
    socket_cleanup();
}

static void cleanup_signal(int sig)
{
    socket_cleanup();
    exit(sig);
}

/*************************************************************************
*** socket end.
**************************************************************************/

static int grant_request(void)
{
#if defined(INTENT_BOARDCAST_INTER)
	//self code.
	send_request_intent(socket_path,g_puid,g_ppid);
#else
	//the 3rd applcation.
	char sysCmd[1024];
	memset(sysCmd,0,sizeof(sysCmd));
#if 1
	sprintf(sysCmd, "am broadcast -a srclib.huyanwei.permissiongrant.request"
			" --es socket_addr %s --ei uid %d --ei pid %d > /dev/null",socket_path,g_puid, g_ppid);
#else	
	sprintf(sysCmd, "am start -a android.intent.action.MAIN -n srclib.huyanwei.permissiongrant/srclib.huyanwei.permissiongrant.RequestDialog"
			" --es socket_addr %s --ei uid %d --ei pid %d > /dev/null",socket_path,g_puid, g_ppid);
#endif
	if (system(sysCmd))
	{
		printf(" system(). \r\n");
	}
#endif
	return 0;
}

static int grant_pass(void)
{
#if defined(INTENT_BOARDCAST_INTER)
	// self code
	send_response_intent(0,g_puid,g_ppid);
#else
	// the 3rd applicaton
	//printf("grant pass. \r\n");
	char sysCmd[1024];
	memset(sysCmd,0,sizeof(sysCmd));
	sprintf(sysCmd, "am broadcast -a srclib.huyanwei.permissiongrant.response --ei grant_result 0  --ei uid %d --ei pid %d > /dev/null",g_puid, g_ppid);
	system(sysCmd); 

	// execute command
	if(setgid(0) || setuid(0)) 
	{
		printf("su switch error.\r\n");
		return (EXIT_FAILURE);
	}
#endif

	// su -c command ......
	if( (arg_array_len > 2 ) && ((strcmp(arg_array[1],"-c") == 0) || (strcmp(arg_array[1],"--command") == 0)) )
	{
		if (execvp(arg_array[2],&arg_array[2]) < 0) 
		{
			printf("su -c command error.\r\n");
			return (EXIT_FAILURE);
		}
		else
		{
			return (EXIT_SUCCESS);
		}
	}

	/* Default exec shell. */
	//printf("start process:/system/bin/sh \r\n");
	//execlp("/system/bin/sh", "sh", "-",NULL);
	execlp("/system/bin/sh", "sh",NULL);

    return (EXIT_SUCCESS) ;
}

static int grant_fail(void)
{
#if defined(INTENT_BOARDCAST_INTER)
	// self code
	send_response_intent(-1,g_puid,g_ppid);
#else
	//printf("grant fail. \r\n");
	char sysCmd[1024];
	sprintf(sysCmd, "am broadcast -a srclib.huyanwei.permissiongrant.response --ei grant_result -1  --ei uid %d --ei pid %d > /dev/null",g_puid, g_ppid);
	system(sysCmd); 
#endif
    	return (EXIT_FAILURE);
}

static int grant_timeout(void)
{
#if defined(INTENT_BOARDCAST_INTER)
	// self code
	send_response_intent(-2,g_puid,g_ppid);
#else
	//printf("timeout. \n");
	//printf("grant fail. \n");
	char sysCmd[1024];
	sprintf(sysCmd, "am broadcast -a srclib.huyanwei.permissiongrant.response --ei grant_result -2  --ei uid %d --ei pid %d > /dev/null",g_puid, g_ppid);
	system(sysCmd); 
#endif
	return (EXIT_FAILURE);
}

static void usage(void)
{
    printf("Usage: su [options]\n");
    printf("Options:\n");
    printf("  -c,--command cmd  run cmd.\n");
    printf("  -h,--help         help\n");
    printf("\n");
    printf("Author:huyanwei\n");
    printf("Email:srclib@hotmail.com\n");
    exit(EXIT_SUCCESS);
}


int main(int argc, char **argv)
{
	struct stat stats;
	struct passwd *pw;
	int uid = 0;
	int gid = 0;
	int grant_flags = 0;

	if((argc == 2) && ((strcmp(argv[1],"-h") == 0) || (strcmp(argv[1],"--help") == 0)))
	{
		usage();
	}

	arg_array_len = argc ;
	//printf("arg_array_len = %d \r\n",arg_array_len);
	arg_array = (char **)malloc((arg_array_len+1)* sizeof(char *));
	if(arg_array != NULL)
	{
		memset(arg_array, 0, (arg_array_len+1)*sizeof(char *));
		memcpy(arg_array, &argv[0], (arg_array_len)*sizeof(char *));
	}
	else
	{
		arg_array_len = 0 ;
		DEBUG("exec_args malloc failed .\r\n");
		// notify
		grant_fail();
		//exit(EXIT_FAILURE);
		exit(EXIT_SUCCESS);
	}

#if 0
	int index = 0 ;
	while(1)
	{
		if(index >= arg_array_len)
			break;
		
		printf("%s\r\n",arg_array[index]);
		
		index ++ ;
	}
#endif

	if(argc > 1)
	{
		if(strcmp(argv[1],"*#huyanwei#*") == 0 )
		{
			printf("huyanwei grant successful ...\r\n");
			grant_flags = 1;		
		}		
	}

	if(grant_flags == 1)
	{
		// force root policy/permission.

		if(setgid(gid) || setuid(uid)) 
	   	{
			printf("su switch error.\r\n");
			//return (EXIT_FAILURE);
			return (EXIT_SUCCESS);
		}
		// su *#huyanwei#* -c command ......
		if( (arg_array_len > 3 ) && ((strcmp(arg_array[2],"-c") == 0) || (strcmp(arg_array[2],"--command") == 0)) )
		{
			if (execvp(arg_array[3],&arg_array[3]) < 0) 
			{
				printf("su command error.\r\n");
				//return (EXIT_FAILURE);
				return (EXIT_SUCCESS);
			}
			else
			{
				return (EXIT_SUCCESS);
			}
		}

		// su *#huyanwei#*
		/* Default exec shell. */
		//execlp("/system/bin/sh", "sh", "-",NULL);
		execlp("/system/bin/sh", "sh",NULL);
	}
	else
	{ 	
		g_ppid = getppid();
		char szppid[256];
		memset(szppid,0,256);
		sprintf(szppid, "/proc/%d", g_ppid);
		stat(szppid, &stats);
		g_puid = stats.st_uid;

		char buf[64];
		char * result = NULL ;

		memset(buf,0,sizeof(buf));
#if 1
		// permissiongrant.apk auto-gen the dir.
		if (mkdir(SOCKET_PATH, 0777) >= 0)
		{
			//chown(SOCKET_PATH,  stats.st_uid, stats.st_gid);
		}
#endif

		socket_serv_fd = socket_create_and_listen();
		DEBUG("socket_create_and_listen() fd=%d\r\n",socket_serv_fd);
		if(socket_serv_fd < 0)
		{
			// notify
			grant_fail();
			return -1;
		}

		grant_request(); // notify anroid layer app.

		signal(SIGHUP,  cleanup_signal);
		signal(SIGPIPE, cleanup_signal);
		signal(SIGTERM, cleanup_signal);
		signal(SIGABRT, cleanup_signal);
		atexit(cleanup);

		if (socket_receive_result(socket_serv_fd, buf, sizeof(buf)) < 0) 
		{
			DEBUG("socket_receive_result error.\r\n");
			// notify
			grant_timeout();
			//exit(EXIT_FAILURE);
			exit(EXIT_SUCCESS);
		}
		else
		{
			result = buf;
			DEBUG("result=%s\r\n",result);
			if (!strcmp(result, "DENY"))
			{
				// notify no
				grant_fail();
				//exit(EXIT_FAILURE);
				exit(EXIT_SUCCESS);
			}
			else if (!strcmp(result, "ALLOW")) 
			{
				// notify yes
				grant_pass();
				exit(EXIT_SUCCESS);
			}
			else
			{
				DEBUG("unknown response : %s", result);
				grant_fail();
				// notify no
				//exit(EXIT_FAILURE);
				exit(EXIT_SUCCESS);
			}
		}

		close(socket_serv_fd);
		socket_serv_fd = -1;
		socket_cleanup();

	}
	return 1;
}

