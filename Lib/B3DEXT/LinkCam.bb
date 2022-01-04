;------------------------------------------------------------------------------------------------
; B3d Extension: LinkCam
;
; LinkPos controllers keep entities centered on the render camera (EXT_RenderCam).
; This is useful for objects like skyboxes that the camera should never approach.
; LinkRot controllers sets the entity's rotation the same as the render camera.
; This is useful for things like UI elements.
;
; EXT_InitLinkPos(ext.EXT_Entity,node)
; EXT_InitLinkRot(ext.EXT_Entity,node)
; EXT_UpdateLinkPos(ext.EXT_Entity)
; EXT_UpdateLinkRot(ext.EXT_Entity)
; EXT_NumLinkPos(ext.EXT_Entity)
; EXT_NumLinkRot(ext.EXT_Entity)
; EXT_DeleteLinkPos(ext.EXT_Entity)
; EXT_DeleteLinkRot(ext.EXT_Entity)
; EXT_DeleteAllLinkPos()
; EXT_DeleteAllLinkRot()
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_LinkPos

	Field entity
	Field nextLinkPos.EXT_LinkPos

End Type

;------------------------------------------------------------------------------------------------
Type EXT_LinkRot

	Field entity
	Field nextLinkRot.EXT_LinkRot

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitLinkPos(ext.EXT_Entity,node)
	
	; make new B3d Extension
	Local link.EXT_LinkPos = New EXT_LinkPos

	; link camera fov to local position of node
	link\entity = node

	; add to LinkPos linked list 
	link\nextLinkPos = ext\LinkPos
	ext\LinkPos = link
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitLinkRot(ext.EXT_Entity,node)
	
	; make new B3d Extension
	Local link.EXT_LinkRot = New EXT_LinkRot

	; link camera fov to local position of node
	link\entity = node

	; add to LinkPos linked list 
	link\nextLinkRot = ext\LinkRot
	ext\LinkRot = link
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateLinkPos(ext.EXT_Entity)
	
	Local link.EXT_LinkPos = ext\linkPos
	
	While (link<>Null)
		PositionEntity(link\entity,EntityX(EXT_RenderCam,1),EntityY(EXT_RenderCam,1),EntityZ(EXT_RenderCam,1),1)
		link = link\nextLinkPos
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateLinkRot(ext.EXT_Entity)
	
	Local link.EXT_LinkRot = ext\linkRot
	
	While (link<>Null)
		RotateEntity(link\entity,EntityPitch(EXT_RenderCam,1),EntityYaw(EXT_RenderCam,1),EntityRoll(EXT_RenderCam,1),1)
		TurnEntity(link\entity,-90,0,0)
		link = link\nextLinkRot
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumLinkPos(ext.EXT_Entity)
;
; Returns the number of LinkPos controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local link.EXT_LinkPos = ext\linkPos
	Local count = 0

	While(link <> Null)
		link  = link\nextLinkPos
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumLinkRot(ext.EXT_Entity)
;
; Returns the number of LinkRot controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local link.EXT_LinkRot = ext\linkRot
	Local count = 0

	While(link <> Null)
		link  = link\nextLinkRot
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteLinkPos(ext.EXT_Entity)
;
; Deletes all LinkPos controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local link.EXT_LinkPos = ext\linkPos
	Local old.EXT_LinkPos
	
	While(link <> Null)
		old = link
		link  = link\nextLinkPos
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteLinkRot(ext.EXT_Entity)
;
; Deletes all LinkRot controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local link.EXT_LinkRot = ext\linkRot
	Local old.EXT_LinkRot
	
	While(link <> Null)
		old = link
		link  = link\nextLinkRot
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllLinkPos()
	
	Delete Each EXT_LinkPos
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllLinkRot()
	
	Delete Each EXT_LinkRot
	
End Function