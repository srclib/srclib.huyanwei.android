/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

#pragma version(1)
#pragma stateVertex(PV)
#pragma stateFragment(PFTexNearest)
#pragma stateStore(PSIcons)

#define PI 3.14159f

int g_SpecialHWWar;

// Attraction to center values from page edge to page center.
float g_AttractionTable[9];
float g_FrictionTable[9];
float g_PhysicsTableSize;

float g_PosPage;
float g_PosVelocity;
float g_LastPositionX;
int g_LastTouchDown;
float g_DT;
int g_LastTime;
int g_PosMax;
int g_DrawCount;
float g_Zoom;
float g_OldPosPage;
float g_OldPosVelocity;
float g_OldZoom;
float g_MoveToTotalTime;
float g_MoveToTime;
float g_MoveToOldPos;

int g_Cols;
int g_Rows;

// Drawing constants, should be parameters ======
#define VIEW_ANGLE 1.28700222f

int g_DrawLastFrame;
int lastFrame(int draw) {
    // We draw one extra frame to work around the last frame post bug.
    // We also need to track if we drew the last frame to deal with large DT
    // in the physics.
    int ret = g_DrawLastFrame | draw;
    g_DrawLastFrame = draw;
    if(!ret)
    {
        g_DrawCount--;
        if(g_DrawCount <= 0)
        {
            //debugF("  drawCount = 0 ", state->drawDone);
            state->drawDone = 1;
        }
    }
    else
    {
        g_DrawCount = 2;
    }
    //debugF("  lastFrame ", ret);
    return ret;  // should return draw instead.
}

void updateReadback() {
    if ((g_OldPosPage != g_PosPage) ||
        (g_OldPosVelocity != g_PosVelocity) ||
        (g_OldZoom != g_Zoom)) {

        g_OldPosPage = g_PosPage;
        g_OldPosVelocity = g_PosVelocity;
        g_OldZoom = g_Zoom;

        int i[3];
        i[0] = g_PosPage * (1 << 16);
        i[1] = g_PosVelocity * (1 << 16);
        i[2] = g_OldZoom * (1 << 16);
        sendToClient(&i[0], 1, 12, 1);
    }
}

void setColor(float r, float g, float b, float a) {
    if (g_SpecialHWWar) {
        color(0, 0, 0, 0.001f);
    } else {
        color(r, g, b, a);
    }
}

void init() {
    g_AttractionTable[0] = 20.0f;
    g_AttractionTable[1] = 20.0f;
    g_AttractionTable[2] = 20.0f;
    g_AttractionTable[3] = 10.0f;
    g_AttractionTable[4] = -10.0f;
    g_AttractionTable[5] = -20.0f;
    g_AttractionTable[6] = -20.0f;
    g_AttractionTable[7] = -20.0f;
    g_AttractionTable[8] = -20.0f;  // dup 7 to avoid a clamp later
    g_FrictionTable[0] = 10.0f;
    g_FrictionTable[1] = 10.0f;
    g_FrictionTable[2] = 11.0f;
    g_FrictionTable[3] = 15.0f;
    g_FrictionTable[4] = 15.0f;
    g_FrictionTable[5] = 11.0f;
    g_FrictionTable[6] = 10.0f;
    g_FrictionTable[7] = 10.0f;
    g_FrictionTable[8] = 10.0f;  // dup 7 to avoid a clamp later
    g_PhysicsTableSize = 7;

    g_PosVelocity = 0;
    g_PosPage = 0;
    g_LastTouchDown = 0;
    g_LastPositionX = 0;
    g_Zoom = 0;
    g_SpecialHWWar = 1;
    g_MoveToTime = 0;
    g_MoveToOldPos = 0;
    g_MoveToTotalTime = 0.2f; // Duration of scrolling 1 line
}

void resetHWWar() {
    g_SpecialHWWar = 1;
}

void move() {
    if (g_LastTouchDown) {
        float dx = -(state->newPositionX - g_LastPositionX);
        g_PosVelocity = 0;
        g_PosPage += dx * 5.2f;

        float pmin = -0.49f;
        float pmax = g_PosMax + 0.49f;
        g_PosPage = clampf(g_PosPage, pmin, pmax);
    }
    g_LastTouchDown = state->newTouchDown;
    g_LastPositionX = state->newPositionX;
    g_MoveToTime = 0;
    //debugF("Move P", g_PosPage);
}

void moveTo() {
    g_MoveToTime = g_MoveToTotalTime;
    g_PosVelocity = 0;
    g_MoveToOldPos = g_PosPage;

	// debugF("======= moveTo", state->targetPos);
}

void setZoom() {
    g_Zoom = state->zoomTarget;
    g_DrawLastFrame = 1;
    updateReadback();
}

void fling() {
    g_LastTouchDown = 0;
    g_PosVelocity = -state->flingVelocity * 4;
    float av = fabsf(g_PosVelocity);
    float minVel = 3.5f;

    minVel *= 1.f - (fabsf(fracf(g_PosPage + 0.5f) - 0.5f) * 0.45f);

    if (av < minVel && av > 0.2f) {
        if (g_PosVelocity > 0) {
            g_PosVelocity = minVel;
        } else {
            g_PosVelocity = -minVel;
        }
    }

    if (g_PosPage <= 0) {
        g_PosVelocity = maxf(0, g_PosVelocity);
    }
    if (g_PosPage > g_PosMax) {
        g_PosVelocity = minf(0, g_PosVelocity);
    }
}

float
modf(float x, float y)
{
    return x-(y*floorf(x/y));
}


/*
 * Interpolates values in the range 0..1 to a curve that eases in
 * and out.
 */
float
getInterpolation(float input) {
    return (cosf((input + 1) * PI) / 2.0f) + 0.5f;
}


void updatePos() {
    if (g_LastTouchDown) {
        return;
    }

    float tablePosNorm = fracf(g_PosPage + 0.5f);
    float tablePosF = tablePosNorm * g_PhysicsTableSize;
    int tablePosI = tablePosF;
    float tablePosFrac = tablePosF - tablePosI;
    float accel = lerpf(g_AttractionTable[tablePosI],
                        g_AttractionTable[tablePosI + 1],
                        tablePosFrac) * g_DT;
    float friction = lerpf(g_FrictionTable[tablePosI],
                        g_FrictionTable[tablePosI + 1],
                        tablePosFrac) * g_DT;

    if (g_MoveToTime) {
        // New position is old posiition + (total distance) * (interpolated time)
        g_PosPage = g_MoveToOldPos + (state->targetPos - g_MoveToOldPos) * getInterpolation((g_MoveToTotalTime - g_MoveToTime) / g_MoveToTotalTime);
        g_MoveToTime -= g_DT;
        if (g_MoveToTime <= 0) {
            g_MoveToTime = 0;
            g_PosPage = state->targetPos;
        }
        return;
    }

    // If our velocity is low OR acceleration is opposing it, apply it.
    if (fabsf(g_PosVelocity) < 4.0f || (g_PosVelocity * accel) < 0) {
        g_PosVelocity += accel;
    }
    //debugF("g_PosPage", g_PosPage);
    //debugF("  g_PosVelocity", g_PosVelocity);
    //debugF("  friction", friction);
    //debugF("  accel", accel);

    // Normal physics
    if (g_PosVelocity > 0) {
        g_PosVelocity -= friction;
        g_PosVelocity = maxf(g_PosVelocity, 0);
    } else {
        g_PosVelocity += friction;
        g_PosVelocity = minf(g_PosVelocity, 0);
    }

    if ((friction > fabsf(g_PosVelocity)) && (friction > fabsf(accel))) {
        // Special get back to center and overcome friction physics.
        float t = tablePosNorm - 0.5f;
        if (fabsf(t) < (friction * g_DT)) {
            // really close, just snap
            g_PosPage = roundf(g_PosPage);
            g_PosVelocity = 0;
        } else {
            if (t > 0) {
                g_PosVelocity = -friction;
            } else {
                g_PosVelocity = friction;
            }
        }
    }

    // Check for out of boundry conditions.
    if (g_PosPage < 0 && g_PosVelocity < 0) {
        float damp = 1.0 + (g_PosPage * 4);
        damp = clampf(damp, 0.f, 0.9f);
        g_PosVelocity *= damp;
    }
    if (g_PosPage > g_PosMax && g_PosVelocity > 0) {
        float damp = 1.0 - ((g_PosPage - g_PosMax) * 4);
        damp = clampf(damp, 0.f, 0.9f);
        g_PosVelocity *= damp;
    }

    g_PosPage += g_PosVelocity * g_DT;
    g_PosPage = clampf(g_PosPage, -0.49, g_PosMax + 0.49);
}

int positionStrip(float row, float column, int isTop, float p, int isText)
{
	/*
	debugF("allapps11_120_1.positionStrip row==", row);
	debugF("allapps11_120_1.positionStrip column==", column);
	debugF("allapps11_120_1.positionStrip isTop==", isTop);
	debugF("allapps11_120_1.positionStrip p==", p);
	*/
	
    float mat1[16];
    
    float x = 0;
    float scale = 0;
    if (getWidth() > getHeight()) {
    	x = 0.5f * (column - 2.5f);
    	scale = 48.f * 3 / getWidth();
    } else {
    	x = 0.5f * (column - 1.5f);
    	scale = 36.f * 3 / getWidth();
    }
    
    if (isTop) {
        matrixLoadTranslate(mat1, x, 0.4f, 0.f);
        matrixScale(mat1, scale, scale, 1.f);
    } else {
        matrixLoadTranslate(mat1, x, -0.555f, 0.f);
        matrixScale(mat1, scale, -scale, 1.f);
    }
    matrixTranslate(mat1, 0, p * 2, 0.f);
    matrixRotate(mat1, -p * 50, 1, 0, 0);
    vpLoadModelMatrix(mat1);

    float soff = -(row * 1.4);
    if (isTop) {
        matrixLoadScale(mat1, 1.f, -0.85f, 1.f);
        if (isText) {
            matrixScale(mat1, 1.f, 2.f, 1.f);
        }
        matrixTranslate(mat1, 0, soff - 0.95f + 0.18f, 0);
    } else {
        matrixLoadScale(mat1, 1.f, 0.85f, 1.f);
        if (isText) {
            matrixScale(mat1, 1.f, 2.f, 1.f);
        }
        matrixTranslate(mat1, 0, soff - 0.65f, 0);
    }
    vpLoadTextureMatrix(mat1);
    return -(soff + 0.3f) * 10.f;
}

void
draw_home_button()
{
    setColor(1.0f, 1.0f, 1.0f, 1.0f);
    bindTexture(NAMED_PFTexNearest, 0, state->homeButtonId);
    float x = (SCREEN_WIDTH_PX - params->homeButtonTextureWidth) / 2;
    float y = (g_Zoom - 1.f) * params->homeButtonTextureHeight;
    
    y -= 17; // move the house to the edge of the screen as it doesn't fill the texture.    
    /*
    debugF("allapps11_120_1.draw_home_button x==", x);
    debugF("allapps11_120_1.draw_home_button y==", y);
    debugF("allapps11_120_1.draw_home_button params->homeButtonTextureWidth==", params->homeButtonTextureWidth);
    debugF("allapps11_120_1.draw_home_button params->homeButtonTextureHeight==", params->homeButtonTextureHeight);    
    */
    drawSpriteScreenspace(x, y, 0, params->homeButtonTextureWidth, params->homeButtonTextureHeight);
}

void drawFrontGrid(float rowOffset, float p)
{
    float h = getHeight();
    float w = getWidth();
    /*
    debugF("allapps11_120_1.drawFrontGrid rowOffset==", rowOffset);
    debugF("allapps11_120_1.drawFrontGrid p== :", p);
    debugF("allapps11_120_1.drawFrontGrid h==", h);
    debugF("allapps11_120_1.drawFrontGrid w==", w);    
    debugF("allapps11_120_1.drawFrontGrid g_Cols==", g_Cols);
    debugF("allapps11_120_1.drawFrontGrid g_Rows==", g_Rows);
    */

    int intRowOffset = rowOffset;
    float rowFrac = rowOffset - intRowOffset;    
	float colWidth = w / g_Cols;        
	float iconWidth = 64.f;
	
	float rowHeight = 0;
	float yoff = 0;
	
    if (w > h) {
    	rowHeight = colWidth - 8;
    	yoff = h - ((h - (rowHeight * g_Rows)) / 2);
    	yoff -= rowHeight;
    } else {
    	rowHeight = colWidth + 7.f;
    	yoff = h - ((h - (rowHeight * g_Rows)) / 2);
    	yoff -= (110 * 3 / 5) - 12;
    }

    int iconNum = intRowOffset * g_Cols;
    float ymax = yoff + rowHeight;
    float ymin = 0; 
    float gridTop = 0;   
    if (w > h) {
    	ymin = -50;
    	gridTop = ymax;
    } else {
    	ymin = yoff - ((g_Rows - 1) * rowHeight) - 35;
    	gridTop = yoff - (3 * 3 / 5);
    }       
        
    float gridBottom = ymin;
    
    if (w > h) {
    	gridBottom += 0;
    } else {
    	gridBottom += 43;
    }
    
	int row, col;
    for (row = 0; row < (g_Rows + 1); row++) {
        float y = yoff - ((-rowFrac + row) * rowHeight);
                
        for (col=0; col < g_Cols; col++) {
            if (iconNum >= state->iconCount) {
                return;
            }

            if (iconNum >= 0) {
                float x = colWidth * col - ((iconWidth - colWidth) / 2);

                if ((y >= ymin) && (y <= ymax)) {
                	float iconY = 0;
                    if (w > h) {
                     	iconY = y;
                     } else {
                     	iconY = y - (20 * 3 / 5);
                     }
                     
                    setColor(1.f, 1.f, 1.f, 1.f);
                    if (state->selectedIconIndex == iconNum && !p) {
                        bindTexture(NAMED_PFTexNearest, 0, state->selectedIconTexture);
                        drawSpriteScreenspace(x, iconY, 0, iconWidth, iconWidth);
                    }

                    bindTexture(NAMED_PFTexNearest, 0, loadI32(ALLOC_ICON_IDS, iconNum));
                    if (!p) {
                        int cropT = 0;
                        if (y > gridTop) {
                            cropT = y - gridTop;
                        }
                        int cropB = 0;
                        if (y < gridBottom) {
                            cropB = gridBottom - y;
                        }
                    	/*
                        debugF("allapps11_120_1.drawFrontGrid row==", row);                  
                        debugF("allapps11_120_1.drawFrontGrid col==", col);
                        debugF("allapps11_120_1.drawFrontGrid x==", x);
                        debugF("allapps11_120_1.drawFrontGrid y==", iconY);
                		*/

                        drawSpriteScreenspaceCropped(x, iconY+cropB, 0, iconWidth, iconWidth-cropT-cropB,
                                0, iconWidth-cropB, iconWidth, -iconWidth+cropT+cropB);
                    } else {                    		                  
	                        float px = 0;
	                        float py = 0;
	                        float d = 0;
	                        if (w > h) {
	                        	px = ((x + iconWidth / 2) - (w / 2)) / (h / 2);	                        
	                        	py = ((iconY + iconWidth / 2) - (h / 2)) / (h / 2);	                        
	                        	d = (iconWidth / 2) / (h / 2);
	                      
	                        } else {
	                        	px = ((x + iconWidth/2) - (getWidth() / 2)) / (getWidth() / 2);	                        
	                        	py = ((iconY + iconWidth/2) - (getHeight() / 2)) / (getWidth() / 2);	                        
	                        	d = (iconWidth/2) / (getWidth() / 2);	                      
	                        }
	                          
	                        px *= p + 1;
	                        py *= p + 1;
	                        
	                        drawQuadTexCoords(px - d, py - d, -p, 0, 1,
	                                          px - d, py + d, -p, 0, 0,
	                                          px + d, py + d, -p, 1, 0,
	                                          px + d, py - d, -p, 1, 1);
	                        /*
	                        debugF("allapps11_120_1.drawFrontGrid iconNum==", iconNum);                  
	                        debugF("allapps11_120_1.drawFrontGrid p==", p);
	                        debugF("allapps11_120_1.drawFrontGrid x==", x);
	                        debugF("allapps11_120_1.drawFrontGrid iconY==", iconY);
                    		debugF("allapps11_120_1.drawFrontGrid px==", px);
                    		debugF("allapps11_120_1.drawFrontGrid py==", py);
                    		debugF("allapps11_120_1.drawFrontGrid d==", d);
                    		*/
                    }
                }
            }
            iconNum++;
        }
    }
}

void drawStrip(float row, float column, int isTop, int iconNum, float p)
{
    if (iconNum < 0) return;
    int offset = positionStrip(row, column, isTop, p, 0);
    bindTexture(NAMED_PFTexMip, 0, loadI32(ALLOC_ICON_IDS, iconNum));
    if (offset < -20) return;
    offset = clamp(offset, 0, 199 - 20);

    int len = 20;
    if (isTop && (offset < 7)) {
        len -= 7 - offset;
        offset = 7;
    }
	
    drawSimpleMeshRange(NAMED_SMMesh, offset * 6, len * 6);
    //drawSimpleMesh(NAMED_SMMesh);
}

void drawTop(float rowOffset, float p)
{
    int row, col;
    int iconNum = 0;
    for (row = 0; row <= (int)(rowOffset+1); row++) {
        for (col=0; col < g_Cols; col++) {
            if (iconNum >= state->iconCount) {
                return;
            }
            drawStrip(rowOffset - row, col, 1, iconNum, p);
            iconNum++;
        }
    }
}

void drawBottom(float rowOffset, float p)
{
    float pos = -1.f;
    int intRowOffset = rowOffset;
    pos -= rowOffset - intRowOffset;

    int row, col;
    int iconNum = (intRowOffset + (g_Rows -1)) * g_Cols;
    while (1) {
        for (col=0; col < g_Cols; col++) {
            if (iconNum >= state->iconCount) {
                return;
            }
            if (pos > -1) {
                drawStrip(pos, col, 0, iconNum, p);
            }
            iconNum++;
        }
        pos += 1.f;
    }
}

int
main(int launchID)
{
    setScriptName("launcher3.rollo");
    /*
    debugF("allapps11_120_1.main launchID==",launchID);
	*/
	
    // Compute dt in seconds.
    int newTime = uptimeMillis();
    //debugF(" now time ", newTime);
    //int diffTime;
    //diffTime = (newTime - g_LastTime);
    g_DT = (newTime - g_LastTime) / 1000.f;
    g_LastTime = newTime;

    //debugF(" draw one frame", diffTime);
    
    if (!g_DrawLastFrame) {
        // If we stopped rendering we cannot use DT.
        // assume 30fps in this case.
        g_DT = 0.033f;
    }
    // physics may break if DT is large.
    g_DT = minf(g_DT, 0.2f);

    if (g_Zoom != state->zoomTarget) {
        float dz;
        if (state->zoomTarget > 0.5f) {
            dz = (1 - g_Zoom) * 0.2f;
        } else {
            dz = -g_DT - (1 - g_Zoom) * 0.2f;
        }
        if (dz && (fabsf(dz) < 0.02f)) {
            if (dz > 0) {
                dz = 0.02f;
            } else {
                dz = -0.02f;
            }
        }
        if (fabsf(g_Zoom - state->zoomTarget) < fabsf(dz)) {
            g_Zoom = state->zoomTarget;
        } else {
            g_Zoom += dz;
        }
        updateReadback();
    }

    // Set clear value to dim the background based on the zoom position.
    if ((g_Zoom < 0.001f) && (state->zoomTarget < 0.001f) && !g_SpecialHWWar) {
        pfClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // When we're zoomed out and not tracking motion events, reset the pos to 0.
        if (!g_LastTouchDown) {
            g_PosPage = 0;
        }
        return lastFrame(0);
    } else {
        pfClearColor(0.0f, 0.0f, 0.0f, g_Zoom);
    }

    // icons & labels
    int iconCount = state->iconCount;
    
    if (getWidth() > getHeight()) {
    	g_Cols = 4;    
    } else {
    	g_Cols = 4;
    }
    g_Rows = 3;

    g_PosMax = ((iconCount + (g_Cols-1)) / g_Cols) - g_Rows;
    if (g_PosMax < 0) g_PosMax = 0;

    updatePos(0.1f);
    updateReadback();

    //debugF("    draw g_PosPage", g_PosPage);

    // Draw the icons ========================================

    /*
    bindProgramFragment(NAMED_PFColor);
    positionStrip(1, 0, 1, 0, 0);
    drawSimpleMesh(NAMED_SMMesh);
    */

    bindProgramFragment(NAMED_PFTexMip);

	if (getWidth() < getHeight()) {
    	drawTop(g_PosPage, 1-g_Zoom);
   		drawBottom(g_PosPage, 1-g_Zoom);
    }

    bindProgramFragment(NAMED_PFTexMip);
    {
        float mat1[16];
        matrixLoadIdentity(mat1);
        vpLoadModelMatrix(mat1);
        vpLoadTextureMatrix(mat1);
    }

    bindProgramFragment(NAMED_PFTexNearest);
    drawFrontGrid(g_PosPage, 1-g_Zoom);
    
    if (getWidth() < getHeight()) 
    {
    	draw_home_button();
    }

    // This is a WAR to do a rendering pass without drawing during init to
    // force the driver to preload and compile its shaders.
    // Without this the first animation does not appear due to the time it
    // takes to init the driver state.
    if (g_SpecialHWWar) {
        g_SpecialHWWar = 0;
        return 1;
    }

    // Bug workaround where the last frame is not always displayed
    // So we keep rendering until the bug is fixed.
    return lastFrame((g_PosVelocity != 0) || fracf(g_PosPage) || g_Zoom != state->zoomTarget || (g_MoveToTime != 0));
}

