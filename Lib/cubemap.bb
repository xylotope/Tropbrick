;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Global cubemappiv
Global cubemapcam
;
Type cubemaptex
	Field entity
	Field detail%
	Field texture
	Field mode%
	Field layer%
	Field side%
	Field entityb
	Field flat%
	Field refr%
	Field alpha#
End Type
;
;;;;;;;;;;;;;;;;;;;;;
; Setup Cubemapping ;
;;;;;;;;;;;;;;;;;;;;;
Function SetCubemapping(n_rizzle%,f_rizzle%,fog_mizzle%,fn_rizzle%,ff_rizzle%,f_riz%,f_giz%,f_biz%)
;
cubemappiv=CreatePivot()
cubemapcam=CreateCamera(cubemappiv)
CameraRange(cubemapcam,n_rizzle,f_rizzle)
CameraFogMode(cubemapcam,fog_mizzle)
CameraFogRange(cubemapcam,fn_rizzle,ff_rizzle)
CameraFogColor(cubemapcam,f_riz,f_giz,f_biz)
;
HideEntity(cubemapcam)
;
End Function
;;;;;;;;;;;;;;;
; Add Cubemap ;
;;;;;;;;;;;;;;;
Function AddCubemap(entity,entity2,mizzode%=1,layer%=0,detail%=128,flizaggs%=128,flizzat%=0,refr%=1,alpha#=1)
;
cmt.cubemaptex=New cubemaptex
cmt\entity=entity
cmt\detail=detail
cmt\texture=CreateTexture(cmt\detail,cmt\detail,flizaggs)
cmt\mode=mizzode
cmt\layer=layer
cmt\side=0
cmt\entityb=entity2
cmt\flat=flizzat
cmt\refr=refr
cmt\alpha=alpha
;
SetCubeMode(cmt\texture,cmt\mode)
EntityTexture(entity2,cmt\texture,0,cmt\layer)
EntityAlpha(entity2,cmt\alpha)
;
End Function
;;;;;;;;;;;;;;;;;;;
; Update Cubemaps ;
;;;;;;;;;;;;;;;;;;;
Function UpdateCubemapTextures(maincam)
;
For cmt.cubemaptex=Each cubemaptex
If EntityInView(cmt\entity,maincam)
;
CameraProjMode(maincam,0)
ShowEntity(cubemapcam)
HideEntity(cmt\entityb)
EntityAlpha(cmt\entityb,0)
;
If cmt\flat=1
	camoffsety#=(EntityY(maincam,True)-EntityY(cmt\entity,True))
	PositionEntity cubemappiv,EntityX#(maincam,True),EntityY#(cmt\entity,True)-(camoffsety),EntityZ(maincam,True)
	PositionEntity(skycube,EntityX(cubemapcam,True),EntityY(cubemapcam,True),EntityZ(cubemapcam,True))
	;RotateEntity camera,-EntityPitch(maincamera),EntityYaw(maincamera),EntityRoll(maincamera)
Else
	PositionEntity cubemappiv,EntityX(cmt\entity,True),EntityY(cmt\entity,True),EntityZ(cmt\entity,True)
	PositionEntity(skycube,EntityX(cubemapcam,True),EntityY(cubemapcam,True),EntityZ(cubemapcam,True))
EndIf
If cmt\mode=3
	PositionEntity cubemappiv,EntityX(cmt\entity,True),EntityY(cmt\entity,True),EntityZ(cmt\entity,True)
	PositionEntity(cubemapcam,0,0,0)
	PositionEntity(skycube,EntityX(cubemapcam,True),EntityY(cubemapcam,True),EntityZ(cubemapcam,True))
EndIf
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
If cmt\refr=0
		CameraViewport cubemapcam,0,0,cmt\detail,cmt\detail
		If cmt\side=0
			SetCubeFace cmt\texture,0:RotateEntity cubemappiv,0,90,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=1
			SaveBuffer(TextureBuffer(cmt\texture),"0.bmp")
		ElseIf cmt\side=1
			SetCubeFace cmt\texture,1:RotateEntity cubemappiv,0,0,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=2
			SaveBuffer(TextureBuffer(cmt\texture),"1.bmp")
		ElseIf cmt\side=2
			SetCubeFace cmt\texture,2:RotateEntity cubemappiv,0,-90,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=3
			SaveBuffer(TextureBuffer(cmt\texture),"2.bmp")
		ElseIf cmt\side=3
			SetCubeFace cmt\texture,3:RotateEntity cubemappiv,0,180,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=4
			SaveBuffer(TextureBuffer(cmt\texture),"3.bmp")
		ElseIf cmt\side=4
			SetCubeFace cmt\texture,4:RotateEntity cubemappiv,-90,0,180:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=5
			SaveBuffer(TextureBuffer(cmt\texture),"4.bmp")
		ElseIf cmt\side=5
			SetCubeFace cmt\texture,5:RotateEntity cubemappiv,90,0,180:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=0
			SaveBuffer(TextureBuffer(cmt\texture),"5.bmp")
		EndIf
Else
		CameraViewport cubemapcam,0,0,cmt\detail,cmt\detail
		If cmt\side=2
			SetCubeFace cmt\texture,2:RotateEntity cubemappiv,0,90,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=3
		ElseIf cmt\side=3
			SetCubeFace cmt\texture,3:RotateEntity cubemappiv,0,0,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=4
		ElseIf cmt\side=0
			SetCubeFace cmt\texture,0:RotateEntity cubemappiv,0,-90,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=1
		ElseIf cmt\side=1
			SetCubeFace cmt\texture,1:RotateEntity cubemappiv,0,180,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=2
		ElseIf cmt\side=4
			SetCubeFace cmt\texture,4:RotateEntity cubemappiv,-90,0,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=5
		ElseIf cmt\side=5
			SetCubeFace cmt\texture,5:RotateEntity cubemappiv,90,0,0:RenderWorld(0)
			CopyRect 0,0,cmt\detail,cmt\detail,0,0,BackBuffer(),TextureBuffer(cmt\texture)
			cmt\side=0
		EndIf
EndIf
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
ShowEntity(cmt\entityb)
EntityAlpha(cmt\entityb,cmt\alpha)
HideEntity(cubemapcam)
CameraProjMode(maincam,1)
;
EndIf
Next
;
End Function
;;;;;;;;;;;;;;;;;
; Free Cubemaps ;
;;;;;;;;;;;;;;;;;
Function FreeCubes()
;
For cmt.cubemaptex=Each cubemaptex
;
FreeTexture(cmt\texture)
Delete cmt 
;
Next
;
End Function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;