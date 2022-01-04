;------------------------------------------------------------------------------------------------
; B3d Extension: Lights
;
; B3d Light Extensions allow export of lights from Max
;
; EXT_InitLight.EXT_Light(ext.EXT_Entity,node,class)
; EXT_NumLights(ext.EXT_Entity)
; EXT_UpdateLights(ext.EXT_Entity)
; EXT_LinkLightColor(light.EXT_Light)
; EXT_LinkLightRange(light.EXT_Light)
; EXT_LinkLightConeAngles(light.EXT_Light)
; EXT_DeleteLights(ext.EXT_Entity)
; EXT_DeleteAllLights()
;
;------------------------------------------------------------------------------------------------

Const EXT_DIRLIGHT 		= 1
Const EXT_OMNILIGHT 	= 2
Const EXT_SPOTLIGHT 	= 3

;------------------------------------------------------------------------------------------------
Type EXT_Light

	Field light					; blitz light handle
	Field class					; type of light
	Field linkRGB				; rgb color controller node
	Field linkRange				; light range controller node
	Field linkFOV				; field of view controller node
	Field nextLight.EXT_Light	; linked list

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitLight.EXT_Light(ext.EXT_Entity,node,class)
;
; initialize the parameters common to all light types
;------------------------------------------------------------------------------------------------
	
 	Local light.EXT_Light = New EXT_Light
	light\class = class

	; get the light node exported from Max
	Local parent = GetParent(node)
	light\light	 = CreateLight(class,parent)
	
	; the light axis is different from Max
	TurnEntity (light\light,90,0,0)
	
	; rename nodes so FindChild() will find the Blitz light
	NameEntity (light\light, EntityName(parent) )
	NameEntity (parent, EntityName(parent)+"_Parent" )

	; the local position of the node controls the rgb color
	; of the light (xpos = red, ypos = green, zpos=blue)
	light\linkRGB = node
	EXT_LinkLightColor(light)

	; light range
	Local range = FindChild(node,"B3DEXT_RANGE")
	If (range)
		light\linkRange = range
		EXT_LinkLightRange(light)
	EndIf

	; spotlight field of view
	If (class = EXT_SPOTLIGHT)
		; the fov controller is the first child of the rgb
		; controller node (xpos = inner, ypos = outer)
		light\linkFOV = FindChild(node,"B3DEXT_FOV") 
		EXT_LinkLightConeAngles(light)
	EndIf
	
	; add to EXT_Entity lights linked list
	light\nextLight = ext\light
	ext\light = light

End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumLights(ext.EXT_Entity)
;
; Returns the number of lights belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local light.EXT_Light = ext\light
	Local count = 0

	While(light<>Null)
		light = light\nextLight
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateLights(ext.EXT_Entity)
	
	Local light.EXT_light = ext\light
	
	While (light<>Null)
		EXT_LinkLightColor(light)
		If (light\linkRange) Then EXT_LinkLightRange(light)
		If (light\class = EXT_SPOTLIGHT) Then EXT_LinkLightConeAngles(light)
		light = light\nextLight
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_LinkLightColor(light.EXT_Light)
	
	Local r#,g#,b#
	Local link = light\linkRGB
	
	r# = ( 255 * EntityX(link) ) Mod 256 
	g# = ( 255 * EntityZ(link) ) Mod 256 
	b# = ( 255 * EntityY(link) ) Mod 256 
	
	LightColor(light\light,r,g,b)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_LinkLightRange(light.EXT_Light)

 	LightRange(light\light,EntityX(light\linkRange))
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_LinkLightConeAngles(light.EXT_Light)

 	Local inner# = EntityX(light\linkFOV)
	Local outer# = EntityZ(light\linkFOV)
 	
 	LightConeAngles(light\light,inner#,outer#)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteLights(ext.EXT_Entity)
;
; Deletes all Light controllers belonging to an EXT_Entity
; NOTE: this doesn't delete the Blitz light entities 
;------------------------------------------------------------------------------------------------

	Local light.EXT_Light = ext\light
	Local old.EXT_Light
	
	While(light <> Null)
		old = light
		light = light\nextLight
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllLights()
;
; Deletes all Light controllers
;------------------------------------------------------------------------------------------------
	
	Delete Each EXT_Light
	
End Function
