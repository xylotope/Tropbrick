;------------------------------------------------------------------------------------------------
; B3d Extension: SubAnims
;
; An EXT_Entity can potentially contain multiple sub animations;
; e.g. multiple skinned meshes mixed with hierarchical animation. 
; Blitz animation functions don't work on these hierarchies, 
; they seem to only update the first anim they find. 
; The B3d Extensions library finds these SubAnims and animates 
; them properly.
;
; EXT_InitSubAnims(ext.EXT_Entity)		; finds all subanims in a hierarchy
; EXT_EnumSubAnims(root,nextSubAnim.EXT_SubAnim,count=0)
; EXT_UpdateSubAnims(ext.EXT_Entity)	; syncs the subanims with animation of the root
; EXT_NumSubAnims(ext.EXT_Entity)		; returns the number of subanims in an EXT_Entity
; EXT_DeleteSubAnims(ext.EXT_Entity)	; deletes all subanims in an EXT_Entity
; EXT_DeleteAllSubAnims()				; deletes all subanims
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_SubAnim

	Field entity						; subanim entity handle			
	Field nextSubAnim.EXT_subAnim		; Linked list

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitSubAnims(ext.EXT_Entity)
;
; Find all subAnims belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------
	
	DebugLog( EXT_LogSection$ )
	DebugLog("EXT_InitSubAnims:")

	Local numAnims = EXT_EnumSubAnims(ext\root,Null)
	
	DebugLog("SubAnims Found:" + numAnims)
	
	If(numAnims) Then ext\subAnim = Last EXT_SubAnim

End Function

;------------------------------------------------------------------------------------------------
Function EXT_EnumSubAnims(root,nextSubAnim.EXT_SubAnim,count=0)
;
; Recurse through hierarchy to build linked list of sub anims
;------------------------------------------------------------------------------------------------

	Local anim.EXT_subAnim
	
 	For i= 1 To CountChildren(root)
 		
 		child = GetChild(root,i)
		
		; does this node have animation?
		If (AnimLength(child)>=0)
			DebugLog(EntityName(child)+": "+AnimLength(child))
 			count 				= count+1	
 			anim 				= New EXT_subAnim
			anim\entity			= child
			anim\nextSubAnim 	= nextSubAnim
			nextSubAnim			= anim
		EndIf
		
		count = EXT_EnumSubAnims(child,nextSubAnim,count)
	
	Next
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateSubAnims(ext.EXT_Entity)
;
; this function transfers the animtime from the root entity to all sub anims
;------------------------------------------------------------------------------------------------
	
	Local anim.EXT_SubAnim = ext\subAnim
	Local time# = AnimTime(ext\root)
	Local seq = AnimSeq(ext\root)

	While(anim<>Null)
		SetAnimTime(anim\entity,time,seq)		
		anim = anim\nextSubAnim
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumSubAnims(ext.EXT_Entity)
;
; returns the number of sub animations in an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local anim.EXT_SubAnim = ext\SubAnim
	Local count = 0

	While(anim<>Null)
		anim  = anim\nextSubAnim
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteSubAnims(ext.EXT_Entity)
;
; Deletes all SubAnims belonging to an EXT_Entity
; NOTE: this doesn't delete the actual Blitz entities
;------------------------------------------------------------------------------------------------

	Local anim.EXT_SubAnim = ext\subAnim
	Local old.EXT_SubAnim
	
	While(anim <> Null)
		old  = anim
		anim = anim\nextSubAnim
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllSubAnims()

	Delete Each EXT_SubAnim
		
End Function