;------------------------------------------------------------------------------------------------
; B3d Extension: Instances
;
; Naming conventions allow the export of instances from Max
; NOTE: the current implementation is fairly primitive 
; If instances are scaled in Max their size is not reliably
; reproduced in Blitz with the current method.
;
; EXT_InitInstances(ext.EXT_Entity,node)
; EXT_EnumInstances(ext.EXT_Entity,node)
; EXT_InitInstance(node)
; EXT_FindInstance(num)
;
;------------------------------------------------------------------------------------------------

Global EXT_InstanceCount = 0

;------------------------------------------------------------------------------------------------
Type EXT_Instance	; temporary list of instances
	Field entity
End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitInstances(ext.EXT_Entity,node)
;
; Find all Instances in this EXT_Entity
;------------------------------------------------------------------------------------------------

	EXT_InstanceCount = 0
	EXT_EnumInstances(ext,node)
	EXT_Log("Instances: " + EXT_InstanceCount)

	; Cleanup
	Delete Each EXT_Instance

End Function

;------------------------------------------------------------------------------------------------
Function EXT_EnumInstances(ext.EXT_Entity,node)
;
; Recursively parse nodes in the hierarchy for Instance tags
;------------------------------------------------------------------------------------------------

	Local i,child
	
	For i = 1 To CountChildren(node)

		child = GetChild(node,i)
 		If (EXT_ParseTag(child,"B3D_INST_")) Then EXT_InitInstance(child)
		If(CountChildren(child)) Then EXT_EnumInstances(ext,child)

	Next
	
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitInstance(node)
;
; A naming convention identifies Instances (B3D_INST_num_...)
; The first occurrence of an Instance in a b3d file contains 
; a mesh (e.g. B3D_INST_0_Box01). Further occurrences of that 
; instance (e.g. B3D_INST_0_Box02) are nodes with no meshes.
; When initializing those instances, a copy of the first
; instance is parented to the empty node.
;
;------------------------------------------------------------------------------------------------

	Local inst.EXT_Instance
	Local copy

	; check the instance list	
 	Local entity = EXT_FindInstance(EXT_ParseNum(node))

	If (Not entity)
		; If this is the first occurunce of an instance,
		; add it to list of instances.
		inst = New EXT_Instance
		inst\entity = node
	Else
		; Otherwise use CopyEntity to copy the instance.
		copy = CopyEntity(entity,node)
		PositionEntity(copy,0,0,0)
		RotateEntity(copy,0,0,0)
		;ScaleEntity(copy,1,1,1)??
	EndIf
	
	EXT_InstanceCount = EXT_InstanceCount + 1
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_FindInstance(num)
;
; Check if this instance exists in the list of instances
;------------------------------------------------------------------------------------------------
	
	Local inst.EXT_Instance
	Local index = 0
	
	For inst = Each EXT_Instance
		If (index = num) Then Return inst\entity
		index = index + 1
	Next
	
	Return 0
	
End Function
