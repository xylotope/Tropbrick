;------------------------------------------------------------------------------------------------
; B3d Extensions Library
; B3d Pipeline v0.6
;
; Release Notes:
;
; - Please send bug reports and suggestions to: pudding@onigirl.com
;
; - You're free to use this library however you want, I would just ask that you
;   share any improvements you make with the Blitz community. Also if you incorporate
;   B3d Extensions in any editor/viewer/media creation software please provide a link to 
;   the B3d Pipeline web page (http://www.onigirl.com/pipeline/)
;
; - This library was developed using the Protean IDE. (http://www.proteanide.co.uk/)
;	I highly recommend this IDE to anyone developing large projects with multiple files.
;
;------------------------------------------------------------------------------------------------

Include "Lib\B3DEXT\Main.bb"			; main functions and documentation

Include "Lib\B3DEXT\AnimBrush.bb"		; animated brush extensions
Include "Lib\B3DEXT\AnimMap.bb"			; animated map extensions
Include "Lib\B3DEXT\AutoHide.bb"		; auto hide based on near/far ranges
Include "Lib\B3DEXT\Billboards.bb"		; billboard controllers
Include "Lib\B3DEXT\Camera.bb"			; camera extensions
Include "Lib\B3DEXT\Environment.bb"		; environment settings (ambient, bgcolor, etc.)
Include "Lib\B3DEXT\Instances.bb"		; instances (using copyentity)
Include "Lib\B3DEXT\Lights.bb"			; light extensions
Include "Lib\B3DEXT\LinkCam.bb"			; link to render cam position/rotation
Include "Lib\B3DEXT\Occlude.bb"			; occlusion controllers
Include "Lib\B3DEXT\Properties.bb"		; blitz3d properties (EntityOrder etc)
Include "Lib\B3DEXT\SubAnims.bb"		; sub animations in hierarchy
Include "Lib\B3DEXT\Visibility.bb"		; visibility controllers
Include "Lib\B3DEXT\XrefScene.bb"		; xref scene support
Include "Lib\B3DEXT\LogFile.bb"			; log file