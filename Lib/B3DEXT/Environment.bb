;------------------------------------------------------------------------------------------------
; B3d Extension: Environment
;
; B3d Environment Extensions allows export of environment settings 
; such as Ambient Color, Background Color, Fog settings, etc. from Max.
; Note: only one environment can be active at a time
;
; EXT_InitBgColor(ext.EXT_Entity,link)
; EXT_InitAmbient(ext.EXT_Entity,link)
; EXT_InitFogColor(ext.EXT_Entity,link)
; EXT_InitFogRange(ext.EXT_Entity,link)
; EXT_UpdateEnvironment()
; EXT_ActivateEnvironment(ext.EXT_Entity)
; EXT_LinkRGB(rgb.EXT_RGB,link)
; EXT_DeleteAllEnvironments()
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_Environment

	Field linkBgColor		; color controllers use the local position 
	Field linkAmbient		; of child nodes to control rgb values
	Field linkFogColor		; e.g. pos (0, 0.5, 1.0) = rgb (0,128,255)
	Field linkFogRange		

End Type

Type EXT_RGB
	Field r#,g#,b#
End Type

;------------------------------------------------------------------------------------------------
;#Region Globals
; These should be considered private library variables
; You shouldn't access these directly 
;------------------------------------------------------------------------------------------------

Global EXT_ActiveEnv.EXT_Environment = New EXT_Environment

Global EXT_BgColor.EXT_RGB 	= New EXT_RGB
Global EXT_Ambient.EXT_RGB 	= New EXT_RGB
Global EXT_FogColor.EXT_RGB	= New EXT_RGB
Global EXT_FogNear#
Global EXT_FogFar#

;#End Region

;------------------------------------------------------------------------------------------------
Function EXT_InitBgColor(ext.EXT_Entity,link)
	
	If(ext\environment=Null) Then ext\environment = New EXT_Environment
	ext\environment\linkBgColor = link
	HideEntity(link)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitAmbient(ext.EXT_Entity,link)
	
	If(ext\environment=Null) Then ext\environment = New EXT_Environment
	ext\environment\linkAmbient = link
	HideEntity(link)

End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitFogColor(ext.EXT_Entity,link)
	
	If(ext\environment=Null) Then ext\environment = New EXT_Environment
	ext\environment\linkFogColor = link
	HideEntity(link)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitFogRange(ext.EXT_Entity,link)
	
	If(ext\environment=Null) Then ext\environment = New EXT_Environment
	ext\environment\linkFogRange = link
	HideEntity(link)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateEnvironment()
;
; Updates the active environment
;------------------------------------------------------------------------------------------------
	
	; Update Ambient Color
	
	If (EXT_ActiveEnv\linkAmbient)
		EXT_LinkRGB(EXT_Ambient,EXT_ActiveEnv\linkAmbient)
		AmbientLight(EXT_Ambient\r#,EXT_Ambient\g#,EXT_Ambient\b#)
	EndIf

	; All other environment extensions need a Camera
	If (EXT_RenderCam = 0) Then Return
	
	; Update Background Color
	
	If (EXT_ActiveEnv\linkBgColor)
		EXT_LinkRGB(EXT_BgColor,EXT_ActiveEnv\linkBgColor)
		CameraClsColor(EXT_RenderCam, EXT_BgColor\r#, EXT_BgColor\g#, EXT_BgColor\b#)
	EndIf
	
	; Update Fog
	
	If (EXT_ActiveEnv\linkFogColor)
		EXT_LinkRGB(EXT_FogColor,EXT_ActiveEnv\linkFogColor)
 		CameraFogColor(EXT_RenderCam, EXT_FogColor\r#, EXT_FogColor\g#, EXT_FogColor\b#)
	EndIf
	
	If (EXT_ActiveEnv\linkFogRange)
		EXT_FogNear# = EntityX(EXT_ActiveEnv\linkFogRange)
		EXT_FogFar#  = EntityZ(EXT_ActiveEnv\linkFogRange)
 		CameraFogRange(EXT_RenderCam, EXT_FogNear#, EXT_FogFar#)
	EndIf

End Function

;------------------------------------------------------------------------------------------------
Function EXT_ActivateEnvironment(ext.EXT_Entity)
;
; Activates the environment belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------
	
	If (ext\environment = Null) Then Return False
	
	EXT_ActiveEnv = ext\environment

	EXT_UpdateRenderCam()
	
	Return True

End Function

;------------------------------------------------------------------------------------------------
Function EXT_LinkRGB(rgb.EXT_RGB,link)
	
	rgb\r# = ( 255 * EntityX(link) ) Mod 256 
	rgb\g# = ( 255 * EntityZ(link) ) Mod 256 
	rgb\b# = ( 255 * EntityY(link) ) Mod 256 
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllEnvironments()
;
; Deletes all Environment controllers
;------------------------------------------------------------------------------------------------

	Delete Each EXT_Environment

End Function
