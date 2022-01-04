;------------------------------------------------------------------------------------------------
; B3d Extension: Camera
; 
; B3d Camera Extensions allow export of cameras from Max
;
; EXT_SetRenderCam(camera)				; sets the camera used by billboards, environment, etc.
; EXT_UpdateRenderCam(	)				; updates environment setting on render cam
; EXT_InitCamera(ext.EXT_Entity,node)	; creates a blitz camera and initializes controllers
; EXT_UpdateCamera(cam.EXT_Camera)		; updates camera controller
; EXT_NumCameras(ext.EXT_Entity)		; returns the number of cameras in an EXT_Entity
; EXT_DeleteCameras(ext.EXT_Entity)		; deletes all camera controllers in an EXT_Entity
; EXT_DeleteAllCameras()				; deletes all camera controllers
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_Camera

	Field camera					; Blitz camera handle
	Field linkFOV					; Field of View controller
	Field nextCamera.EXT_Camera		; Linked list

End Type

;------------------------------------------------------------------------------------------------
; Globals
; These should be considered private library variables
; You shouldn't access these directly 
;------------------------------------------------------------------------------------------------

Global EXT_RenderCam

;------------------------------------------------------------------------------------------------
Function EXT_SetRenderCam(camera)
;
; Many B3d Extensions need a camera to work with (e.g. EXT_Environment, EXT_Billboard,...)
; Use EXT_SetRenderCam(camera) to set the camera to be used by these extensions.
;------------------------------------------------------------------------------------------------

	EXT_RenderCam = camera
	EXT_UpdateRenderCam()

End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateRenderCam()
;
; Updates environment setting on render cam
;------------------------------------------------------------------------------------------------
	
	If (EXT_RenderCam = 0) Then Return
	If (EXT_ActiveEnv=Null) Then Return

	If (EXT_ActiveEnv\linkFogColor)
		CameraFogMode(EXT_RenderCam,1)
	EndIf

End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitCamera(ext.EXT_Entity,node)
;
; Creates a Blitz camera and initializes camera controllers
;------------------------------------------------------------------------------------------------

	Local cam.EXT_Camera 	= New EXT_Camera

	; the parent of the "B3DEXT_CAMERA" node is 
	; the original Max camera node; It retains 
	; the position and rotation of the original 
	; Max camera.
	camnode = GetParent(node)
	
	; make a new camera and parent it to camnode
	; to inherit position and rotation keys.
	cam\camera = CreateCamera(camnode)
	
	; rename the new camera and camnode so that
	; FindChild commands will return the camera
	; and not camnode.
	name$ = EntityName(camnode)
	NameEntity(cam\camera, name$)
	NameEntity(camnode, name$+"_Parent") 

	; link camera fov to local position of node
	cam\linkFOV = node

	; point camera down right axis
	RotateEntity(cam\camera,90,0,0)
	
	; turn off camera
	CameraProjMode(cam\camera,0)
	
	; add to ext camera linked list 
	cam\nextCamera = ext\camera
	ext\camera = cam

End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateCamera(cam.EXT_Camera)
;
; Updates camera controllers
;------------------------------------------------------------------------------------------------

	Local fov#

	If(cam\linkFOV)
		fov# = EntityX(cam\linkFOV)
		CameraZoom(cam\camera, 1.0/Tan(fov#/2.0))
	EndIf

End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumCameras(ext.EXT_Entity)
;
; Returns the number of cameras belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local cam.EXT_Camera = ext\camera
	Local count = 0

	While(cam <> Null)
		cam  = cam\nextCamera
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteCameras(ext.EXT_Entity)
;
; Deletes all Camera controllers belonging to an EXT_Entity
; NOTE: this doesn't delete the Blitz camera entities 
;------------------------------------------------------------------------------------------------

	Local cam.EXT_Camera = ext\camera
	Local old.EXT_Camera
	
	While(cam <> Null)
		old = cam
		cam  = cam\nextCamera
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllCameras()
;
; Deletes all Camera controllers
;------------------------------------------------------------------------------------------------
	
	Delete Each EXT_Camera
	
End Function