;Graphics3D 1024,768,32,2
;SetBuffer BackBuffer()
SeedRnd MilliSecs()
;;;;;;;;;;;;;;;;;;;;
Include "Lib\B3dExtensions.bb"
Include "Lib\ColorRef.bb"
Include "Lib\DevilParticleSystem.bb"
Include "Lib\LIB_juicy_fonts.bb"
;;;;;;;;;;;;;;;;;;;;
Global gW=GraphicsWidth()
Global gH=GraphicsHeight()
Global gW2=gW/2
Global gH2=gH/2
HidePointer()
;;;;;;;;;;;;;;;;;;;;
Global gameCam=CreateCamera()
Global gameListener=CreateListener(gameCam,.01,1,1)
Global gameLight=CreateLight()
Global gameLightS=CreateLight(2)
Global gameTimer=CreateTimer(30)
CameraClsColor(gameCam,0,127,255)
CameraProjMode(gameCam,0)
CameraViewport(gameCam,gW/4,0,gW-gW/4,gH)
LightColor(gameLightS,1,1,1)
;;;;;;;;;;;;;;;;;;;;
Global pause_key=25
;;;;;;;;;;;;;;;;;;;;
Global mXSpeed#
;;;;;;;;;;;;;;;;;;;;
Global gamePanel=LoadImage("HUD\panel.bmp")
Global gameMouse=LoadImage("MenuObjects\mouse.bmp")
;;;;;;;;;;;;;;;;;;;;
Global gamePause
;;;;;;;;;;;;;;;;;;;;
Global gameLand
Global gameWater
Global gameWaterBump
Global gameSky
Global gameSky2
Global gameTurtle
Global gameFont=jf_load_font("MenuObjects\font.png")
Global gameFontBar=LoadImage("MenuObjects\nameBar.bmp")
Global gameFontBa2=LoadImage("MenuObjects\nameBar2.bmp")
Global gameOpenMenu=LoadImage("MenuObjects\openMenu.bmp")
Global gameOpenMenuB=LoadImage("MenuObjects\openMenuB.bmp")
Global gameFileSlider=LoadImage("MenuObjects\fileSlider.bmp")
Global gameLetters
Global gamePName$
Global gameIName
Global gameCTex
Global gameWTex
Global gameWTimer
MidHandle(gameFontBar)
MidHandle(gameFontBa2)
Dim dFiles$(1)
;;;;;;;;;;;;;;;;;;;;
Global gamePlayer
Global gamePlayerX#
Global gamePlayerY#
Global gamePlayerScore
Global gameScoreMultiplier
Global gamePlayerHealth
Global gameHealthImage=LoadImage("MenuObjects\coconuts.bmp")
Global gameShellsImage=LoadImage("MenuObjects\shells.bmp")
Global gameBall
Global gameLevel
Global gameBrickCount
Global gamePUpCount
Global gameRoundTime
Global gameExit
;;;;;;;;;;;;;;;;;;;;
Global gamePUpsImage=LoadAnimImage("MenuObjects\MenuPowerUps\full.bmp",32,32,0,7)
Dim gamePUps(63)
;;;;;;;;;;;;;;;;;;;;
; Pirate Stuff
;;;;;;;;;;;;;;;;;;;;
Global gameX
Global gamePirate1=CreateCube()
Global gamePirate2=CreateCube()
HideEntity(gamePirate1)
HideEntity(gamePirate2)
Global parrotTimer = 0
Global parrotTime = 100
Global activateChest = 0
;;;;;;;;;;;;;;;;;;;;
; Game Songs
;;;;;;;;;;;;;;;;;;;;
Global levelSong
Global gameMusic1
Global gameMusic2
Global gameMusic3
Global gameMusic4
Global gameChnlMusic
;;;;;;;;;;;;;;;;;;;;
Global gameAmbient1
Global gameChnlAmbient
;;;;;;;;;;;;;;;;;;;;
; 3D Game Sounds
;;;;;;;;;;;;;;;;;;;;
Global soundPHit
Global soundBHit
Global soundBreak
Global soundSand
Global soundCannonShoot
Global soundCannonS2
Global soundCannonHit
Global soundSquak1
Global soundSquak2
Global soundCoin1
Global soundCreak1
Global soundFare1
Global soundLose1
Global soundNew1
Global soundLoad1
Global soundCancel1
Global soundWater1
Global soundPick1
LoadSounds()
;;;;;;;;;;;;;;;;;;;;
; Game Shell Globals
;;;;;;;;;;;;;;;;;;;;
Global gameShellCount%
;;;;;;;;;;;;;;;;;;;;
; Power Up Globals
;;;;;;;;;;;;;;;;;;;;
Global gamePlayerScale#
Global gameBallCount%
Global gameGuardActive%
Global gameCannonsActive%
Global gameCannonsTime%
Global gameBoatCount%
;;;;;;;;;;;;;;;;;;;;
Global gameBricks1
Global gameBricks2
Global gameBricks3
Global gameBricks4
Global gameBricks5
Global gameBricks6
Global gameBricks7
;;;;;;;;;;;;;;;;;;;;
Global brickEmitter
Global brickTemplate,brickSubT
Global treasureTemplate
Global powerUpEmitter
Global powerUpTemplate
Global shellEmitter
Global ShellTemplate
;;;;;;;;;;;;;;;;;;;;
Global gamePowerUp
;;;;;;;;;;;;;;;;;;;;
Const playerColl=1,ballColl=2,brickColl=3,wallColl=4,topColl=5,powerColl=6,brickHColl=7,bulletColl=8,shellColl=9,boatColl=10
Collisions(ballColl,playerColl,2,2)
Collisions(ballColl,brickColl,2,2)
Collisions(ballColl,wallColl,2,2)
Collisions(ballColl,topColl,2,2)
Collisions(ballColl,brickHColl,2,2)
Collisions(ballColl,shellColl,1,2)
Collisions(bulletColl,brickColl,2,2)
Collisions(powerColl,playerColl,2,2)
Collisions(boatColl,boatColl,1,2)
;Collisions(shellColl,ballColl,1,2)
;;;;;;;;;;;;;;;;;;;;
Type balls
	Field entity
	Field main
	Field hit
	Field xSpeed#
	Field ySpeed#
	Field time%
	Field timer%
	Field life%
	Field lifeTimer
End Type
Type bricks
	Field entity
	Field hits
	Field value%
End Type
Type bullets
	Field entity
	Field speed#
	Field hit
	Field time
	Field timer
End Type
Type characters
	Field character
	Field enter
	Field c$
End Type
Type letters
	Field sletter$
End Type
Type parrots
	Field entity
	Field time
	Field timer
	Field speed#
End Type
Type pirates
	Field entity
	Field side
	Field distTraveled#
	Field distTurned#
	Field destDist#
	Field travelSpeed#
	Field travelDist#
End Type
Type powerUp
	Field entity
	Field sprite
	Field active
	Field pType
	Field time
	Field timer
End Type
Type saves
	Field path$
	Field number
	Field level
	Field image
End Type
Type shells
	Field entity
End Type
Type tikis
	Field entity
	Field light
	Field sprite
End Type
Type trees
	Field entity
End Type
;;;;;;;;;;;;;;;;;;;;
Global gameWidth
Global gameHeight
;;;;;;;;;;;;;;;;;;;;
Function Game(chooseMenu%=2)
;;;;;;;;;;;;;;
;DancingCredits()
;chooseMenu=1
Local success=0
If chooseMenu=1
	success=AskName(13)
	If success=3
		Goto label1
	EndIf
ElseIf chooseMenu=2
	success=ChooseFile()
	If success=3
		Goto label1
	EndIf
Else
	gameExit=1
EndIf
;;;;;;;;;;;;;;;;;;;;
l_surface=CreateSprite(gameCam)
gameWidth=gW-gW/4
gameHeight=gH;Global 

SpriteViewMode l_surface,2
PositionEntity l_surface,0,0,1.001
EntityOrder l_surface,-90
ScaleSprite l_surface, 1,1
EntityAlpha l_surface, .4

l_texture=CreateTexture(width,Height,1+256)
ScaleTexture l_texture, (Float TextureWidth(l_texture)/Float gameWidth),(Float TextureHeight(l_texture)/Float gameHeight)

EntityTexture l_surface,l_texture
TextureBlend l_texture,2
;;;;;;;;;;;;;;;;;;;;
gameLevel = 1
InitParticles(gameCam)
gamePlayerHealth=3
If chooseMenu = 1
	WriteFile("Saves\"+gamePName+"save.sbg")
Else
	loadFile=ReadFile("Saves\"+gamePName+"save.sbg")
EndIf
If loadFile
	gameLevel=ReadInt(loadFile)
	gamePlayerHealth=ReadInt(loadFile)
	gamePlayerScore=ReadInt(loadFile)
	gameShellCount=ReadInt(loadFile)
	CloseFile(loadFile)
EndIf
;gameLevel=23
If gameLevel = 1
	Tutorial()
EndIf
StartLevel(gameLevel)
;;;;;;;;;;;;;;;;;;;;
blur = True
;;;;;;;;;;;;;;;;;;;;
Repeat
Cls

If KeyHit(48)
	blur = Not blur
	If blur = True
		ShowEntity(l_surface)
	Else
		HideEntity(l_surface)
	EndIf
EndIf
uG = UpdateGame()
If uG = 12
	gameExit = 12
EndIf
If gamePlayerHealth<1
	gameExit = 2
EndIf
UpdateParticles()
UpdateWorld()
l_update(2);RenderWorld()
DrawHud()
WaitTimer(gameTimer)
If gamePause=1
	OptionsMenu()
	gamePause = 0
	;PauseGame()
EndIf
Flip
Until gameExit
FreeGame()
If gameExit = 12
	FlushKeys()
	FlushMouse()
	DancingCredits()
	FlushKeys()
	FlushMouse()
	result = CompareHighScores(gamePName, gamePlayerScore)
	FlushKeys()
	;WaitKey()
	HighScoreTable(result)
ElseIf gameExit = 2
	Fail()
EndIf
.label1
gameExit = 0
;ClearWorld()
;FreeImage(gamePanel)
;FreeImage(gameMouse)
;FreeImage(gameHealthImage)
;FreeSound(gameMusic1)
;FreeSound(gameMusic2)
;FreeSound(gameMusic3)
;FreeSound(gameMusic4)
End Function
;;;;;;;;;;;;;;;;;;;;
Function AskName(length%)
	PlaySound(soundNew1)
	;;;;;;;;;;;;;;;;;;;
	myDir=0
	;myDir=ReadDir(currentDirectory$)
	exists = 0
	;;;;;;;;;;;;;;;;;;;
	For letter.letters = Each letters
		Delete letter
	Next
	For char.Characters = Each Characters
		Delete char
	Next
	gamePName$=""
	gameLetters = 0
	backTex=LoadImage("Levels\watertex.jpg")
	newGImage=LoadImage("MenuObjects\newGame.bmp")
	fWarning = LoadImage("MenuObjects\fWarning.bmp")
	sYes = LoadImage("MenuObjects\sYes.bmp")
	sNo = LoadImage("MenuObjects\sNo.bmp")
	mousePointer = LoadImage("MenuObjects\pointer.bmp")
	goButton = LoadAnimImage("MenuObjects\goButton.bmp", 64, 64, 0, 2)
	aNCancel = LoadAnimImage("MenuObjects\cancelA.bmp", 250, 128, 0, 2)
	entName = LoadImage("MenuObjects\entName.bmp")
	MaskImage(entName, 255, 0, 255)
	HandleImage(entName, 127, 0)
	HandleImage(anCancel, 125, 0)
	MaskImage(goButton, 255, 0, 255)
	HandleImage(newGImage,249,0)
	MidHandle(goButton)
	Repeat
	mouseOver = 0
	If exists <> 1
		If gameLetters>0
			If KeyHit(28)
				.TryName
				;FlushKeys()
				;WaitKey()
				;;;;;;;;;;;;;;;;;
				myDir=ReadDir("Saves\") 
				Repeat
				;
				file$=NextFile$(myDir)
				;
				If file$=""
					Exit
				EndIf
				;
				If Right(file, 3) = "sbg"
					If Lower(gamePName) = Lower(Left(file, (Len(file)-8)))
						;FlushKeys()
						;WaitKey()
						exists = 1
						Exit
					EndIf
				EndIf
				;
				Forever
				;
				CloseDir myDir
				myDir = 0
				;;;;;;;;;;;;;;;;;
				If exists = 0
					gameIName=jf_create_text(gameFont,127,63,gamePName,1,1)
					For char.Characters = Each Characters
						Delete char
					Next
					Exit
				EndIf
			EndIf
		EndIf
		gamePName$=GetInformation$(length)
	EndIf
	
	If KeyHit(1)
		For char.Characters = Each Characters
			Delete char
		Next
		Return(3)
		Exit
	EndIf
	
	bTX=bTX+1
	bTY=bTY-1
	TileImage(backTex,bTX,bTY)
	DrawImage(gameFontBa2,gW2,gH2)
	For char.Characters=Each Characters
		jf_text(gameFont,gW2,gH2,gamePName,1,1)
	Next
	DrawImage(gameFontBar, gW2, gH2)
	DrawImage(newGImage, gW2, 127)
	DrawImage(entName, gW2, 260)
	mMX = MouseX()
	mMY = MouseY()
	If exists = 1
		DrawImage(fWarning, 255, 300)
		;DrawImage(mousePointer, mMX, mMY)
		If ImageRectOverlap(mousePointer, mMX, mMY, 320, 511, 145, 10)
			mouseOver = 2
			DrawImage(sYes, 320, 474)
		ElseIf ImageRectOverlap(mousePointer, mMX, mMY, 561, 513, 145, 10)
			mouseOver = 3
			DrawImage(sNo, 561, 476)
		EndIf
	Else
		If RectsOverlap(mMX, mMY, 1, 1, 680, 352, 64, 64)
			mouseOver = 4
			DrawImage(goButton, gW2 + 200, gH2, 1)
		Else
			DrawImage(goButton, gW2 + 200, gH2, 0)
		EndIf
		If RectsOverlap(mMX, mMY, 1, 1, 388, 476, 247, 90)
			mouseOver = 5
			DrawImage(aNCancel, 511, 450, 1)
		Else
			DrawImage(aNCancel, 511, 450, 0)
		EndIf
	EndIf
	DrawImage(mousePointer, mMX, mMY)
	If MouseDown(1)
		If mouseOver = 5
			For char.Characters = Each Characters
				Delete char
			Next
			Return(3)
			Exit
		ElseIf mouseOver = 4
			If gameLetters > 0
				Goto TryName
			EndIf
		ElseIf mouseOver = 3
			exists = 0
		ElseIf mouseOver = 2
			gameIName=jf_create_text(gameFont,127,63,gamePName,1,1)
			For char.Characters = Each Characters
				Delete char
			Next
			Exit
		EndIf
	EndIf
	WaitTimer(gameTimer)
	Flip
	Forever
	FreeImage(backTex)
	FreeImage(newGImage)
	FreeImage(fWarning)
	FreeImage(sYes)
	FreeImage(sNo)
	FreeImage(mousePointer)
	FreeImage(goButton)
	FreeImage(aNCancel)
	FreeImage(entName)
	FlushKeys()
End Function
;;;;;;;;;;;;;;;;;;;;
Function GetInformation$(length%)
	key=GetKey()
	If key>96
	If key<123
		char.Characters = New Characters
		key = key - 96
		FlushKeys()
	EndIf
	Else If key > 47
		If key<91
			If key > 64
				char.Characters = New Characters
				key = key - 38
				FlushKeys()
			ElseIf key < 58
				char.Characters = New Characters
				key = 0;key - 20
			EndIf
		EndIf
	ElseIf key = 32
		char.Characters = New Characters
		key = 0;key - 5
		FlushKeys()
	ElseIf key = 8
		char.Characters = Last Characters
		key=-1
		;Delete char
	;	FlushKeys()
	ElseIf key = 13
		char.Characters = New Characters
		key = 0;key + 14
		char\enter = 1
		FlushKeys()
	Else
		key=0
	EndIf
	;key=key+1
	If key>0 And key<53
		If gameLetters<length
		letter.letters=New letters
		gameLetters=gameLetters+1
		Select key
			Case 1
				letter\sletter$="a"
			Case 2
				letter\sletter$="b"
			Case 3
				letter\sletter$="c"
			Case 4
				letter\sletter$="d"
			Case 5
				letter\sletter$="e"
			Case 6
				letter\sletter$="f"
			Case 7
				letter\sletter$="g"
			Case 8
				letter\sletter$="h"
			Case 9
				letter\sletter$="i"
			Case 10
				letter\sletter$="j"
			Case 11
				letter\sletter$="k"
			Case 12
				letter\sletter$="l"
			Case 13
				letter\sletter$="m"
			Case 14
				letter\sletter$="n"
			Case 15
				letter\sletter$="o"
			Case 16
				letter\sletter$="p"
			Case 17
				letter\sletter$="q"
			Case 18
				letter\sletter$="r"
			Case 19
				letter\sletter$="s"
			Case 20
				letter\sletter$="t"
			Case 21
				letter\sletter$="u"
			Case 22
				letter\sletter$="v"
			Case 23
				letter\sletter$="w"
			Case 24
				letter\sletter$="x"
			Case 25
				letter\sletter$="y"
			Case 26
				letter\sletter$="z"
			Case 27
				letter\sletter$="A"
			Case 28
				letter\sletter$="B"
			Case 29
				letter\sletter$="C"
			Case 30
				letter\sletter$="D"
			Case 31
				letter\sletter$="E"
			Case 32
				letter\sletter$="F"
			Case 33
				letter\sletter$="G"
			Case 34
				letter\sletter$="H"
			Case 35
				letter\sletter$="I"
			Case 36
				letter\sletter$="J"
			Case 37
				letter\sletter$="K"
			Case 38
				letter\sletter$="L"
			Case 39
				letter\sletter$="M"
			Case 40
				letter\sletter$="N"
			Case 41
				letter\sletter$="O"
			Case 42
				letter\sletter$="P"
			Case 43
				letter\sletter$="Q"
			Case 44
				letter\sletter$="R"
			Case 45
				letter\sletter$="S"
			Case 46
				letter\sletter$="T"
			Case 47
				letter\sletter$="U"
			Case 48
				letter\sletter$="V"
			Case 49
				letter\sletter$="W"
			Case 50
				letter\sletter$="X"
			Case 51
				letter\sletter$="Y"
			Case 52
				letter\sletter$="Z"
		End Select
		EndIf
		;If gameLetters=1
			;letter\sletter$=Upper$(letter\sletter$)
		;EndIf
	ElseIf key=-1
		If gameLetters>0
			gameLetters=gameLetters-1
			letter.letters=Last letters
			Delete letter
		EndIf
	EndIf
	gameLName$=""
	For letter.letters=Each letters
		gameLName$=gameLName$+letter\sletter$
	Next
	Return(gameLName$)
End Function
;;;;;;;;;;;;;;;;;;;;
Function Keys()
	If KeyHit(1)
		gameExit=1
	EndIf
	;If KeyHit(33)
	;	windowed = Not windowed
	;	If windowed = 1
	;		Graphics3D width, height, depth, 2
	;	Else
	;		windowed = 0
	;		Graphics3D width, height, depth, 1
	;	EndIf
	;EndIf
	If KeyHit(pause_key)
		FlushKeys()
		FlushMouse()
		gamePause=1
	EndIf
	mXSpeed#=MouseXSpeed()
	MoveMouse(gW2,gH2)
	MouseXSpeed()
	mXSpeed=mXSpeed*.1
	gamePlayerX=gamePlayerX+mXSpeed
	If gamePlayerX>65
		gamePlayerX=65
	ElseIf gamePlayerX<-65
		gamePlayerX=-65
	EndIf
	PositionEntity(gamePlayer,gamePlayerX,0,-70)
	;MoveEntity(gamePlayer,mXSpeed,0,0)
	mXSpeed=mXSpeed/15
	If gameCannonsActive
		gameCannonsTime=gameCannonsTime+1
		If gameCannonsTime>30
			If MouseHit(1)
				gameCannonsTime=0
				For x=-5 To 5 Step 10
					bullet.bullets=New bullets
					bullet\entity=CreateSphere(6)
					bullet\speed#=1
					bullet\hit=0
					bullet\time=0
					bullet\timer=120
					PositionEntity(bullet\entity,x+EntityX(gamePlayer,True),1,-65)
					EntityColor(bullet\entity,63,63,63)
					EntityShininess(bullet\entity,1)
					EntityType(bullet\entity,bulletColl)
					number = Rand(1,2)
					If number = 1
						EmitSound(soundCannonShoot,bullet\entity)
					Else
						EmitSound(soundCannonS2, bullet\entity)
					EndIf
				Next
			EndIf
		EndIf
	EndIf
	FlushMouse()
End Function
;;;;;;;;;;;;;;;;;;;;
Function DrawHud()
	DrawImage(gamePanel,0,0)
	jf_draw_text(gameIName)
	;jf_text(gameFont,gW2,gH2,Hex(color_Red),1,1)
	;jf_text(gameFont,gW2,gH2,mXSpeed,1,1)
	Select gamePlayerHealth
		Case 1
			DrawImageRect(gameHealthImage,10,127,0,0,23,21)
		Case 2
			DrawImageRect(gameHealthImage,10,127,0,0,46,21)
		Case 3
			DrawImageRect(gameHealthImage,10,127,0,0,69,21)
		Case 4
			DrawImageRect(gameHealthImage,10,127,0,0,92,21)
		Case 5
			DrawImageRect(gameHealthImage,10,127,0,0,115,21)
		Case 6
			DrawImageRect(gameHealthImage,10,127,0,0,138,21)
		Case 7
			DrawImageRect(gameHealthImage,10,127,0,0,161,21)
		Case 8
			DrawImageRect(gameHealthImage,10,127,0,0,184,21)
		Case 9
			DrawImageRect(gameHealthImage,10,127,0,0,207,21)
		Case 10
			DrawImageRect(gameHealthImage,10,127,0,0,230,21)
			;jf_text(gameFont,127,160,"Health Full",1,1)
	End Select
	Select gameShellCount
		Case 1
			DrawImageRect(gameShellsImage,34,331,0,0,21,21)
		Case 2
			DrawImageRect(gameShellsImage,34,331,0,0,42,21)
		Case 3
			DrawImageRect(gameShellsImage,34,331,0,0,63,21)
		Case 4
			DrawImageRect(gameShellsImage,34,331,0,0,84,21)
		Case 5
			DrawImageRect(gameShellsImage,34,331,0,0,105,21)
		Case 6
			DrawImageRect(gameShellsImage,34,331,0,0,126,21)
		Case 7
			DrawImageRect(gameShellsImage,34,331,0,0,147,21)
		Case 8
			DrawImageRect(gameShellsImage,34,331,0,0,168,21)
		Case 9
			DrawImageRect(gameShellsImage,34,331,0,0,189,21)
		Case 10
			DrawImageRect(gameShellsImage,34,331,0,0,210,21)
	End Select
	Local gameTimeF#
	gameTimeF#=gameRoundTime
	gameTimeF#=gameTimeF/1800
	Local gameTimeF2#
	gameTimeF2=gameTimeF
	If gameTimeF>=20
		gameTimeF=20
	ElseIf gameTimeF>=19
		gameTimeF=19
	ElseIf gameTimeF>=18
		gameTimeF=18
	ElseIf gameTimeF>=17
		gameTimeF=17
	ElseIf gameTimeF>=16
		gameTimeF=16
	ElseIf gameTimeF>=15
		gameTimeF=15
	ElseIf gameTimeF>=14
		gameTimeF=14
	ElseIf gameTimeF>=13
		gameTimeF=13
	ElseIf gameTimeF>=12
		gameTimeF=12
	ElseIf gameTimeF>=11
		gameTimeF=11
	ElseIf gameTimeF>=10
		gameTimeF=10
	ElseIf gameTimeF>=9
		gameTimeF=9
	ElseIf gameTimeF>=8
		gameTimeF=8
	ElseIf gameTimeF>=7
		gameTimeF=7
	ElseIf gameTimeF>=6
		gameTimeF=6
	ElseIf gameTimeF>=5
		gameTimeF=5
	ElseIf gametimeF>=4
		gameTimeF=4
	ElseIf gameTimeF>=3
		gametimeF=3
	ElseIf gameTimeF>=2
		gameTimeF=2
	ElseIf gameTimeF>=1
		gameTimeF=1
	Else
		gameTimeF=0
	EndIf
	Local gameTimeI%
	gameTimeI%=gameTimeF
	Local gameRTimeS
	gameRTimeS=((gameRoundTime Mod 1800)/30)
	jf_text(gameFont,127,114,"Lives: "+gamePlayerHealth,1,1)
	jf_text(gameFont,127,191,"Level: "+gameLevel,1,1)
	jf_text(gameFont,127,256,"Score: "+gamePlayerScore,1,1)
	jf_text(gameFont,127,319,"Shells: "+gameShellCount,1,1)
	jf_text(gameFont,127,400,"Power Ups",1,1);383
	;jf_text(gameFont,400,114,"Tri Count: "+TrisRendered(),1,1)
	;jf_text(gameFont,640,31,"Power Up Count"+gamePUpCount,1,1)
	If gameRTimeS>9
		jf_text(gameFont,640,10,gameTimeI+":"+gameRTimeS,1,1)
	Else
		jf_text(gameFont,640,10,gameTimeI+":"+"0"+gameRTimeS,1,1)
	EndIf
	For x=0 To gamePUpCount
		pUpsNumber=gamePUps(x)
		pUpsNumber=pUPsNumber-1
		If pUpsNumber>-1
			iX=13+(x*33)-33
			iYF#=x
			iYF#=iYF/7
			iY=Ceil(iYF)
			iY2=iY
			iY=iY*38+415
			If iY2=2
				iX=iX-231
			ElseIf iY2=3
				iX=iX-462
			ElseIf iY2=4
				iX=iX-693
			ElseIf iY2=5
				iX=iX-924
			ElseIf iY2=6
				iX=iX-1155
			ElseIf iY2=7
				iX=iX-1386
			ElseIf iY2=8
				iX=iX-1617
			ElseIf iY2=9
				iX=iX-1848
			EndIf
			DrawImage(gamePUpsImage,iX,iY,pUpsNumber)
		EndIf
	Next
	For x=0 To 31
		gamePUps(x)=0
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function UpdateGame()
	gameWTimer=gameWTimer+1
	PositionTexture(gameWTex,gameWTimer*.001,0)
	gCamX=EntityX(gameCam,True)
	gCamY=EntityY(gameCam,True)
	gCamZ=EntityZ(gameCam,True)
	;PositionEntity(gameSky,gCamX,gCamY,gCamZ)
	Keys()
	;
	For ball.balls=Each balls
			For shell.shells=Each shells
				If EntityCollided(ball\entity,shellColl)
					If CollisionEntity(ball\entity,1)=shell\entity
					gameShellCount=gameShellCount+1
					contx=EntityX(shell\entity)
					conty=EntityY(shell\entity)
					contz=EntityZ(shell\entity)
					PositionEntity(shellEmitter,contx,conty,contz)
					SetEmitter(shellEmitter,shellTemplate, True)
					If shell\entity
						FreeEntity(shell\entity)
					EndIf
					Delete shell
					EndIf
				EndIf
			Next
		If gameBallCount<2
			ball\main=1
		EndIf
		;If Not ball\main
		;	ball\life=ball\life+1
		;	If ball\life>ball\lifeTimer
		;		FreeEntity(ball\entity)
		;		Delete ball
		;		gameBallCount=gameBallCount-1
		;	EndIf
		;EndIf
		ball\hit=0
		ball\timer=ball\timer+1
		If ball\timer>-1
			TranslateEntity(ball\entity,ball\xSpeed,0,ball\ySpeed)
		If ball\timer>2
			ballYaw=EntityYaw(ball\entity,True)
			If EntityCollided(ball\entity,playerColl)
				ball\timer=0
				ball\ySpeed=(-1)*(ball\ySpeed)
				ball\xSpeed=(ball\xSpeed)+(mXSpeed)
				TranslateEntity(ball\entity,0,0,ball\ySpeed)
				EmitSound(soundPHit,gamePlayer)
			ElseIf EntityCollided(ball\entity,brickHColl)
				ball\timer=0
				ball\xSpeed=(-1)*(ball\xSpeed)
				TranslateEntity(ball\entity,ball\xSpeed,0,0)
				ball\hit=CollisionEntity(ball\entity,1)
			ElseIf EntityCollided(ball\entity,brickColl)
				ball\timer=0
				ball\ySpeed=(-1)*(ball\ySpeed)
				TranslateEntity(ball\entity,0,0,ball\ySpeed)
				ball\hit=CollisionEntity(ball\entity,1)
			ElseIf EntityCollided(ball\entity,wallColl)
				ball\timer=0
				ball\xspeed=(-1)*(ball\xSpeed)
				TranslateEntity(ball\entity,ball\xSpeed*2,0,0)
				EmitSound(soundSand,ball\entity)
			ElseIf EntityCollided(ball\entity,topColl)
				ball\timer=0
				ball\ySpeed=(-1)*(ball\ySpeed)
				TranslateEntity(ball\entity,0,0,ball\ySpeed)
				If CollisionEntity(ball\entity,1)=FindChild(gameLand,"Box03")
					EmitSound(soundSand,ball\entity)
				Else
					EmitSound(soundPHit,ball\entity)
				EndIf
			EndIf
		EndIf
		If ball\xSpeed>1.1
			ball\xSpeed=ball\xSpeed-.02
		ElseIf ball\ySpeed<-1.1
			ball\xSpeed=ball\xSpeed+.02
		EndIf
		If ball\xSpeed>2
			ball\xSpeed=2
		ElseIf ball\xSpeed<-2
			ball\xSpeed=-2
		EndIf
		If ball\ySpeed>2
			ball\ySpeed=2
		ElseIf ball\ySpeed<-2
			ball\ySpeed=-2
		EndIf
		;TranslateEntity(ball\entity,ball\XSpeed,0,ball\YSpeed)
		;If EntityCollided(ball\entity,playerColl)
		;	ball\XSpeed=(ball\XSpeed+(mXSpeed*.5))
		;	ball\YSpeed=(ball\YSpeed)*(-1)
		;	TranslateEntity(ball\entity,0,0,ball\YSpeed)
		;ElseIf EntityCollided(ball\entity,brickColl)
		;	ball\YSpeed=(ball\YSpeed)*(-1)
		;	TranslateEntity(ball\entity,0,0,ball\YSpeed)
		;ElseIf EntityCollided(ball\entity,wallColl)
		;	ball\XSpeed=(ball\XSpeed)*(-1)
		;	TranslateEntity(ball\entity,ball\XSpeed,0,0)
		;ElseIf EntityCollided(ball\entity,topColl)
		;	ball\YSpeed=(ball\YSpeed)*(-1)
		;	TranslateEntity(ball\entity,0,0,ball\YSpeed)
		;EndIf
		loc_ballY=EntityY(ball\entity,True)
		If loc_ballY<0
			TranslateEntity(ball\entity,0,(loc_ballY)*(-1),0)
		ElseIf loc_ballY>0
			TranslateEntity(ball\entity,0,-1,0)
		EndIf
		If EntityZ(ball\entity,True)<-80
			If gameBallCount<2
				gamePlayerHealth=gamePlayerHealth-1
				HideEntity(ball\entity)
				PositionEntity(ball\entity,0,0,-50)
				UpdateWorld()
				ShowEntity(ball\entity)
				ball\XSpeed#=1
				ball\YSpeed#=-1
				ball\time%=-1
				ball\timer%=-30
			Else
				If ball\entity
					FreeEntity(ball\entity)
				EndIf
				Delete ball
				gameBallCount=gameBallCount-1
			EndIf
			If gamePlayerHealth<0
				gamePlayerHealth=0
			EndIf
		EndIf
		EndIf
	Next
	For bullet.bullets=Each bullets
		bullet\hit=0
		bullet\time=bullet\time+1
		MoveEntity(bullet\entity,0,0,bullet\speed)
		If EntityCollided(bullet\entity,brickColl)
			bullet\hit=CollisionEntity(bullet\entity,1)
			;ScaleEntity(bullet\hit,.01,.01,.01)
			;WaitKey()
			;If bullet\entity
			;	FreeEntity(bullet\entity)
			;EndIf
			;Delete(bullet)
		EndIf
		If bullet<>Null
		If bullet\time>bullet\timer
			If bullet\entity
				FreeEntity(bullet\entity)
			EndIf
			Delete(bullet)
		EndIf
		EndIf
	Next
	For brick.bricks=Each bricks
		brkEntity=brick\entity
		brkHits=brick\hits
		For ball.balls=Each balls
			If brick<>Null
			If ball\hit=FindChild(brkEntity,"Box02") Or ball\hit=FindChild(brkEntity,"Box01")
				brick\hits=brick\hits-1
				gamePlayerScore=gamePlayerScore+10*gameScoreMultiplier
				If brick\hits=7
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks7,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits=6
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks6,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits=5
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks5,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits=4
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks4,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits=3
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks3,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits=2
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks2,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits=1
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks1,0,0)
					EmitSound(soundBHit,brick\entity)
				ElseIf brick\hits<1
					For pirate.pirates = Each pirates
						If pirate\side = 2
							pirate\destDist = pirate\destDist + pirate\travelDist
						EndIf
					Next
					EmitSound(soundBreak,brick\entity)
					contx=EntityX(brick\entity)
					conty=EntityY(brick\entity)
					contz=EntityZ(brick\entity)
					PositionEntity(brickEmitter,contx,conty,contz)
					SetEmitter(brickEmitter,brickTemplate, True);End
					randNum=0
					randNum=Rand(1,100)
					If randNum>82 And randNum<86
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\plus.dds",5,pUp\entity)
						pUp\pType=1
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum>92 And randNum<96
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\minus.dds",5,pUp\entity)
						pUp\pType=2
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=71 Or randNum=72
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\coconuts.dds",5,pUp\entity)
						pUp\pType=3
						pUp\time=0
						pUp\timer=80
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=67 Or randNum=68
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\guard.dds",5,pUp\entity)
						pUp\pType=4
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=50 Or randNum=51
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\cannon.dds",5,pUp\entity)
						pUp\pType=5
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum = 47 Or randNum = 48
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\coins.dds",5,pUp\entity)
						pUp\pType=7
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum < 20
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\ship.dds",5,pUp\entity)
						pUp\pType=6
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum < 40
						If gameBrickCount<10
							pUp.powerUp=New powerUp
							pUp\entity=CopyEntity(gamePowerUp)
							pUp\sprite=LoadSprite("Bricks\ship.dds",5,pUp\entity)
							pUp\pType=6
							pUp\time=0
							pUp\timer=900
							EntityRadius(pUp\entity,7,7)
							EntityType(pUp\entity,powerColl)
							PositionEntity(pUp\entity,contx,4,contz)
							EntityBlend(pUp\sprite,1)
							ScaleSprite(pUp\sprite,6,6)
						EndIf
					EndIf
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
					gamePlayerScore=gamePlayerScore+(brick\value*gameScoreMultiplier)
					If brick\entity
						FreeEntity(brick\entity)
						brick\entity=0
					EndIf
					gameBrickCount=gameBrickCount-1
					Delete brick
				EndIf
			EndIf
			EndIf
		Next
		For bullet.bullets=Each bullets
			If brick<>Null
				;If bullet\hit<>0
					;WaitKey()
				;EndIf
			If bullet\hit=FindChild(brkEntity,"Box02") Or bullet\hit=FindChild(brkEntity,"Box01") Or bullet\hit=brkEntity
				brick\hits=brick\hits-1
				gamePlayerScore=gamePlayerScore+10*gameScoreMultiplier
				If brick\hits=7
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks7,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits=6
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks6,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits=5
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks5,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits=4
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks4,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits=3
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks3,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits=2
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks2,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits=1
					EntityTexture(FindChild(brick\entity,"Box02"),gameBricks1,0,0)
					EmitSound(soundCannonHit,brick\entity)
				ElseIf brick\hits<1
					For pirate.pirates = Each pirates
						If pirate\side = 2
							pirate\destDist = pirate\destDist + pirate\travelDist
						EndIf
					Next
					EmitSound(soundCannonHit,brick\entity)
					contx=EntityX(brick\entity)
					conty=EntityY(brick\entity)
					contz=EntityZ(brick\entity)
					PositionEntity(brickEmitter,contx,conty,contz)
					SetEmitter(brickEmitter,brickTemplate, True);End
					randNum=0
					randNum=Rand(1,100)
					If randNum>82 And randNum<86
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\plus.dds",5,pUp\entity)
						pUp\pType=1
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum>92 And randNum<96
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\minus.dds",5,pUp\entity)
						pUp\pType=2
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=71 Or randNum=72
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\coconuts.dds",5,pUp\entity)
						pUp\pType=3
						pUp\time=0
						pUp\timer=80
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=67 Or randNum=68
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\guard.dds",5,pUp\entity)
						pUp\pType=4
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=50 Or randNum=51
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\cannon.dds",5,pUp\entity)
						pUp\pType=5
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum=47 Or randNum=48
						pUp.powerUp=New powerUp
						pUp\entity=CopyEntity(gamePowerUp)
						pUp\sprite=LoadSprite("Bricks\coins.dds",5,pUp\entity)
						pUp\pType=7
						pUp\time=0
						pUp\timer=900
						EntityRadius(pUp\entity,7,7)
						EntityType(pUp\entity,powerColl)
						PositionEntity(pUp\entity,contx,4,contz)
						EntityBlend(pUp\sprite,1)
						ScaleSprite(pUp\sprite,6,6)
					ElseIf randNum<41
						If gameBrickCount<10
							pUp.powerUp=New powerUp
							pUp\entity=CopyEntity(gamePowerUp)
							pUp\sprite=LoadSprite("Bricks\ship.dds",5,pUp\entity)
							pUp\ptype=6
							pUp\time=0
							pUp\timer=900
							EntityRadius(pUp\entity,7,7)
							EntityType(pUp\entity,powerColl)
							PositionEntity(pUp\entity,contx,4,contz)
							EntityBlend(pUp\sprite,1)
							ScaleSprite(pUp\sprite,6,6)
						EndIf
					EndIf
					;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
					gamePlayerScore=gamePlayerScore+brick\value*gameScoreMultiplier
					If brick\entity
						FreeEntity(brick\entity)
						brick\entity=0
					EndIf
					gameBrickCount=gameBrickCount-1
					Delete brick
				EndIf
				If bullet\entity
					FreeEntity(bullet\entity)
				EndIf
				If bullet<>Null
					Delete bullet
				EndIf
			EndIf
			EndIf
		Next
	Next
	boatNumber=0
	gameScoreMultiplier=1
	gamePUpCount=0
	For pUp.powerUp=Each powerUp
		If pUp\active=1
			gamePUpCount=gamePUpCount+1
			gamePUps(gamePUpCount)=pUp\pType
			Select pUp\pType
				Case 1
					If pUp\time>pUp\timer-50
						If gamePlayerScale>1
							gamePlayerScale=gamePlayerScale-.1
							ScaleEntity(gamePlayer,gamePlayerScale,1,1)
						EndIf
					Else
						If gamePlayerScale<2
							gamePlayerScale=gamePlayerScale+.1
							ScaleEntity(gamePlayer,gamePlayerScale,1,1)
						EndIf
					EndIf
				Case 2
					If pUp\time>pUp\timer-50
						If gamePlayerScale<1
							gamePlayerScale=gamePlayerScale+.1
							ScaleEntity(gamePlayer,gamePlayerScale,1,1)
						EndIf
					Else
						If gamePlayerScale>.5
							gamePlayerScale=gamePlayerScale-.1
							ScaleEntity(gamePlayer,gamePlayerScale,1,1)
						EndIf
					EndIf
				Case 3
					If gameBallCount<20
						If (pUp\time Mod 20)=1
							ball.balls=New balls
							ball\entity=CopyEntity(gameBall)
							ball\main=1
							ball\XSpeed#=Rnd(-1,1)
							ball\YSpeed#=-1
							ball\time%=-1
							ball\timer%=-10
							HideEntity(ball\entity)
							PositionEntity(ball\entity,Rand(5,9),45,90)
							UpdateWorld()
							ShowEntity(ball\entity)
							gameBallCount=gameBallCount+1
							EntityRadius(ball\entity,2)
						EndIf
					EndIf
				Case 4
					If Not pUp\entity
						pUp\entity=LoadMesh("Bricks\guard.b3d")
						PositionEntity(pUp\entity,0,0,-75)
						ScaleEntity(pUp\entity,1,1,1)
						EntityType(pUp\entity,topColl)
					EndIf
				Case 5
					If gameCannonsActive = 0
						If Not pUp\entity
							gameCannonsActive=1
							pUp\entity=LoadAnimMesh("Bricks\cannons.b3d")
							Animate(pUp\entity,3,-.1)
							;EntityParent(pUp\entity,gamePlayer)
							;PositionEntity(pUp\entity,0,0,0)
							PositionEntity(pUp\entity,EntityX(gamePlayer,True),EntityY(gamePlayer,True),EntityZ(gamePlayer,True))
							TurnEntity(pUp\entity,0,180,0)
						EndIf
					Else
						If pUp\entity
							PositionEntity(pUp\entity,EntityX(gamePlayer,True),EntityY(gamePlayer,True),EntityZ(gamePlayer,True))
						EndIf
					EndIf
				Case 6
					If Not pUp\entity
						pUp\entity=LoadAnimMesh("Bricks\ship.b3d")
						PositionEntity(pUp\entity,Rand(-70,70),0,-100)
						ScaleEntity(pUp\entity,.5,.5,.5)
						EntityType(pUp\entity,boatColl)
						ghost=5
						EntityRadius(pUp\entity,ghost)
						gameBoatCount=gameBoatCount+1
					EndIf
					boatNumber=boatNumber+1
					brickNumber=0
					For brick.bricks=Each bricks
						brickNumber=brickNumber+1
						If brickNumber=boatNumber
							If brick<>Null
							boatTarg=brick\entity
							EndIf
						EndIf
					Next
					If boatTarg
					boatDistX=Abs(EntityX(pUp\entity,True))-Abs(EntityX(boatTarg,True))
					boatDistY=Abs(boatDistX)
					entityDistY=(EntityZ(pUp\entity,True))-(EntityZ(boatTarg,True))
					;entityDistY=Abs(entityDistY)
					boatYaw=EntityYaw(pUp\entity,True)
					boatY=EntityY(pUp\entity,True)
					If boatY<>0
						TranslateEntity(pUp\entity,0,-boatY,0)
					EndIf
					If EntityX(pUp\entity,True)-EntityX(boatTarg,True)>5
						PointEntity(pUp\entity,boatTarg)
						TranslateEntity(pUp\entity,-.5,0,0)
						TurnEntity(pUp\entity,0,90,0)
					ElseIf EntityX(pUp\entity,True)-EntityX(boatTarg,True)<-5
						TranslateEntity(pUp\entity,.5,0,0)
						PointEntity(pUp\entity,boatTarg)
						TurnEntity(pUp\entity,0,90,0)
					Else
						If (pUp\time Mod 30)=1
						For x=-5 To 5 Step 5
							bullet.bullets=New bullets
							bullet\entity=CreateSphere(6)
							If EntityZ(pUp\entity,True)>EntityZ(boatTarg,True)
								bullet\speed#=-1
							Else
								bullet\speed#=1
							EndIf
							bullet\hit=0
							bullet\time=0
							bullet\timer=120
							PositionEntity(bullet\entity,x+EntityX(pUp\entity,True),1,EntityZ(pUp\entity,True))
							EntityColor(bullet\entity,63,63,63)
							EntityShininess(bullet\entity,1)
							EntityType(bullet\entity,bulletColl)
							EmitSound(soundCannonShoot,bullet\entity)
						Next
						EndIf
					EndIf
					If EntityZ(pUp\entity,True)-EntityZ(boatTarg,True)<5
						TranslateEntity(pUp\entity,0,0,.5)
					EndIf
					EndIf
				Case 7
					gameScoreMultiplier=gameScoreMultiplier*2
				Case 8
					For ball.balls=Each balls
						If ball\time>ball\timer-1
							If ball\ySpeed<0
								ball\ySpeed=-1
							ElseIf ball\ySpeed>0
								ball\ySpeed=1
							EndIf
						Else
							ball\ySpeed=ball\ySpeed*1.5
							If ball\ySpeed>1.5
								ball\ySpeed=1.5
							ElseIf ball\ySpeed<-1.5
								ball\ySpeed=-1.5
							EndIf
						EndIf
						;If ball\ySpeed>0
						;	If ball\time>ball\timer-10
						;		ball\ySpeed#=ball\ySpeed-.1
						;		If ball\ySpeed<1
						;			ball\ySpeed=1
						;		EndIf
						;	Else
						;		If ball\ySpeed#<2
						;			ball\ySpeed#=ball\ySpeed#+.1
						;		EndIf
						;	EndIf
						;ElseIf ball\ySpeed<0
						;	If ball\time>ball\timer-10
						;		ball\ySpeed#=ball\ySpeed+.1
						;		If ball\ySpeed>(-1)
						;			ball\ySpeed=(-1)
						;		EndIf
						;	Else
						;		If ball\ySpeed#<(-2)
						;			ball\ySpeed#=ball\ySpeed#-(.1)
						;		EndIf
						;	EndIf
						;EndIf
					Next
			End Select
			pUp\time=pUp\time+1
			If pUp\time>pUp\timer
				If pUp\entity
					FreeEntity(pUp\entity)
					pUp\entity = 0
				EndIf
				If pUp\sprite
					FreeEntity(pUp\sprite)
					pUp\sprite = 0
				EndIf
				If pUp\pType=5
					gameCannonsActive=0
				ElseIf pUp\pType=6
					gameBoatCount=gameBoatCount-1
				EndIf
				Delete pUp
			EndIf
		Else
			MoveEntity(pUp\entity,0,0,-.5)
			If EntityCollided(pUp\entity,playerColl)
				pUp\active=1
				contx=EntityX(pUp\entity)
				conty=EntityY(pUp\entity)
				contz=EntityZ(pUp\entity)
				PositionEntity(powerUpEmitter,contx,conty,contz)
				SetEmitter(powerUpEmitter,powerUpTemplate, True);End
				If pUp\sprite
					FreeEntity(pUp\sprite)
					pUp\sprite = 0
					;FlushKeys()
					;WaitKey()
				EndIf
				If pUp\entity
					FreeEntity(pUp\entity)
					pUp\entity = 0
				EndIf
			EndIf
		EndIf
	Next
	If gameScoreMultiplier<1
		gameScoreMultiplier=1
	EndIf
	If gameShellCount>9
		gameShellCount=0
		gamePlayerHealth=gamePlayerHealth+1
	EndIf
	tempScore=gamePlayerScore Mod 1000000
	If gamePlayerScore>0
		If tempScore=0
			gamePlayerHealth=gamePlayerHealth+1
			gamePlayerScore=gamePlayerScore+1000
		EndIf
	EndIf
	If gamePlayerHealth>10
		gamePlayerHealth=10
	EndIf
	gameRoundTime=gameRoundTime-1
	If gameRoundTime<0
		gameRoundTime=0
	EndIf
	If KeyHit(49)
		Return 12
	EndIf
	If gameBrickCount < 1; And gameRoundTime<1
		gameLevel=gameLevel+1
		EmitSound(soundFare1, gamePlayer)
		BonusPoints(gameRoundTime)
		If gameLevel > 90
			Return 12
		Else
			StartLevel(gameLevel)
		EndIf
	EndIf
	UpdatePirates()
	UpdateParrots()
End Function
;;;;;;;;;;;;;;;;;;;;
Function StartLevel(levelNum%=1)
	If gameX
		FreeEntity(gameX)
		gameX = 0
	EndIf
	gameX=LoadSprite("Levels\x.bmp",1+4)
	EntityFX(gameX,1)
	;EntityOrder(gameX,-80)
	PositionEntity(gameX,0,19,85)
	ScaleSprite(gameX,5,5)
	SpriteViewMode(gameX,2)
	TurnEntity(gameX,70,0,0)
	gameRoundTime=5400
	levelSong=levelNum Mod 4
	If levelSong=0
		levelSong=4
	EndIf
	If ChannelPlaying(gameChnlMusic)
		StopChannel(gameChnlMusic)
	EndIf
	If ChannelPlaying(gameChnlAmbient)
		StopChannel(gameChnlAmbient)
	EndIf
	Select levelSong
		Case 1
			gameChnlMusic=PlaySound(gameMusic1)
		Case 2
			gameChnlMusic=PlaySound(gameMusic2)
		Case 3
			gameChnlMusic=PlaySound(gameMusic3)
		Case 4
			gameChnlMusic=PlaySound(gameMusic4)
	End Select
	gameChnlAmbient = PlaySound(gameAmbient1)
	;;;;;
	saveFile=WriteFile("Saves\"+gamePName+"save.sbg")
	WriteInt(saveFile,levelNum)
	WriteInt(saveFile,gamePlayerHealth)
	WriteInt(saveFile,gamePlayerScore)
	WriteInt(saveFile,gameShellCount)
	CloseFile(saveFile)
	If gamePlayer<>0
		FreeEntity(gamePlayer)
		gamePlayer = 0
	EndIf
	If gameBall<>0
		FreeEntity(gameBall)
		gameBall = 0
	EndIf
	If gamePowerUp<>0
		FreeEntity(gamePowerUp)
		gamePowerUp = 0
	EndIf
	gamePlayer=LoadAnimMesh("Bricks\player.b3d")
	gamePlayerX=20
	gamePlayerScale=1
	gameBall=LoadMesh("Bricks\coconut.b3d")
	gamePowerUp=LoadMesh("Bricks\bubble.b3d")
	gameLevel=levelNum
	ClearPowerUps()
	;gameCannonsActive=0
	EntityType(FindChild(gamePlayer,"Box01"),playerColl)
	EntityType(gameBall,ballColl)
	PositionEntity(gamePlayer,0,0,-70)
	PositionEntity(gameBall,0,0,-50)
	HideEntity(gameBall)
	ScaleEntity(gamePowerUp,.14,.14,.14)
	HideEntity(gamePowerUp)
	If brickEmitter
		FreeEntity(brickEmitter)
		brickEmitter=0
		FreeEmitters()
	EndIf
	brickEmitter=CreatePivot()
	If brickTemplate
		FreeTemplate(brickTemplate)
		brickTemplate = 0
	EndIf
	;Brick break template - blue-ish
	brickTemplate = CreateTemplate()
	SetTemplateEmitterBlend(brickTemplate, 1)
	SetTemplateInterval(brickTemplate, 1)
	SetTemplateParticlesPerInterval(brickTemplate, 8)
	SetTemplateEmitterLifeTime(brickTemplate, 1)
	SetTemplateParticleLifeTime(brickTemplate, 60, 75)
	SetTemplateTexture(brickTemplate, "Sprites\coin.dds", 1+2)
	SetTemplateOffset(brickTemplate, -.4, .4, -.4, .4, -.4, .4)
	SetTemplateVelocity(brickTemplate, -.4, .4, -.4, 16, -.4, .4)
	SetTemplateRotation(brickTemplate, -3, 3)
	SetTemplateGravity(brickTemplate, .02)
	SetTemplateSize(brickTemplate, .9, 1, .9, 1);.8, .4, .5, 2)
	SetTemplateFloor(brickTemplate, 55, .45)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 3)
	SetTemplateInterval(t0, 1)
	SetTemplateParticlesPerInterval(t0, 8)
	SetTemplateEmitterLifeTime(t0, 1)
	SetTemplateParticleLifeTime(t0, 60, 75)
	SetTemplateTexture(t0, "Sprites\ruby.dds")
	SetTemplateOffset(t0, -.4, .4, -.4, .4, -.4, .4)
	SetTemplateVelocity(t0, -.4, .4, -.4, 16, -.4, .4)
	SetTemplateRotation(t0, -3, 3)
	SetTemplateGravity(t0, .02)
	SetTemplateSize(t0, .9, 1, .9, 1);.8, .4, .5, 2)
	SetTemplateFloor(t0, 55, .45)
	SetTemplateColors(t0, $FF0000, $FF0000)
	SetTemplateSubTemplate(brickTemplate, t0)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 1)
	SetTemplateInterval(t0, 1)
	SetTemplateParticlesPerInterval(t0, 30)
	SetTemplateEmitterLifeTime(t0, 1)
	SetTemplateParticleLifeTime(t0, 70, 85)
	SetTemplateTexture(t0, "Sprites\emerald.dds", 1+2)
	SetTemplateOffset(t0, -.2, .2, -.2, .2, -.2, .2)
	SetTemplateVelocity(t0, -.1, .1, -.1, .2, -.1, .1)
	SetTemplateAlignToFall(t0, True, 45)
	SetTemplateGravity(t0, .0015)
	SetTemplateAlphaVel(t0, True)
	SetTemplateSize(t0, .9, 1, .9, 1)
	SetTemplateColors(t0, $0000FF, $0000FF)
	SetTemplateBrightness(t0, 8)
	SetTemplateSubTemplate(brickTemplate, t0)
	If treasureTemplate
		FreeTemplate(treasureTemplate)
		treasureTemplate = 0
	EndIf
	;Treasure break template - blue-ish
	treasureTemplate = CreateTemplate()
	SetTemplateEmitterBlend(treasureTemplate, 1)
	SetTemplateInterval(treasureTemplate, 1)
	SetTemplateParticlesPerInterval(treasureTemplate, 1)
	SetTemplateEmitterLifeTime(treasureTemplate, -1)
	SetTemplateParticleLifeTime(treasureTemplate, 60, 75)
	SetTemplateTexture(treasureTemplate, "Sprites\coin.dds", 1+2)
	SetTemplateOffset(treasureTemplate, -.4, .4, -.4, .4, -.4, .4)
	SetTemplateVelocity(treasureTemplate, -1.4, 1.4, -1.4, 16, -1.4, 1.4)
	SetTemplateRotation(treasureTemplate, -3, 3)
	SetTemplateGravity(treasureTemplate, .02)
	SetTemplateSize(treasureTemplate, 1.4, 1.5, 1.4, 1.5);.8, .4, .5, 2)
	SetTemplateFloor(treasureTemplate, 55, .45)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 3)
	SetTemplateInterval(t0, 1)
	SetTemplateParticlesPerInterval(t0, 1)
	SetTemplateEmitterLifeTime(t0, -1)
	SetTemplateParticleLifeTime(t0, 60, 75)
	SetTemplateTexture(t0, "Sprites\ruby.dds")
	SetTemplateOffset(t0, -.4, .4, -.4, .4, -.4, .4)
	SetTemplateVelocity(t0, -.4, .4, -.4, 16, -.4, .4)
	SetTemplateRotation(t0, -3, 3)
	SetTemplateGravity(t0, .02)
	SetTemplateSize(t0, .9, 1, .9, 1);.8, .4, .5, 2)
	SetTemplateFloor(t0, 55, .45)
	SetTemplateColors(t0, $FF0000, $FF0000)
	SetTemplateSubTemplate(treasureTemplate, t0)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 1)
	SetTemplateInterval(t0, 1)
	SetTemplateParticlesPerInterval(t0, 1)
	SetTemplateEmitterLifeTime(t0, -1)
	SetTemplateParticleLifeTime(t0, 70, 85)
	SetTemplateTexture(t0, "Sprites\emerald.dds", 1+2)
	SetTemplateOffset(t0, -.2, .2, -.2, .2, -.2, .2)
	SetTemplateVelocity(t0, -.1, .1, -.1, .2, -.1, .1)
	SetTemplateAlignToFall(t0, True, 45)
	SetTemplateGravity(t0, .0015)
	SetTemplateAlphaVel(t0, True)
	SetTemplateSize(t0, .9, 1, .9, 1)
	SetTemplateColors(t0, $0000FF, $0000FF)
	SetTemplateBrightness(t0, 8)
	SetTemplateSubTemplate(treasureTemplate, t0)
	If powerUpEmitter
		FreeEntity(powerUpEmitter)
		powerUpEmitter=0
	EndIf
	powerUpEmitter=CreatePivot()
	If powerUpTemplate
		FreeTemplate(powerUpTemplate)
		powerUpTemplate = 0
	EndIf
	;Power up template - Red
	powerUpTemplate = CreateTemplate()
	SetTemplateEmitterBlend(powerUpTemplate, 3)
	SetTemplateInterval(powerUpTemplate, 1)
	SetTemplateParticlesPerInterval(powerUpTemplate, 30)
	SetTemplateEmitterLifeTime(powerUpTemplate, 1)
	SetTemplateParticleLifeTime(powerUpTemplate, 70, 85)
	SetTemplateTexture(powerUpTemplate, "Sprites\spark.dds", 3)
	SetTemplateOffset(powerUpTemplate, -.2, .2, -.2, .2, -.2, .2)
	SetTemplateVelocity(powerUpTemplate, -.2, .2, -.2, .4, -.2, .2)
	SetTemplateAlignToFall(powerUpTemplate, True, 45)
	SetTemplateGravity(powerUpTemplate, .0015)
	SetTemplateAlphaVel(powerUpTemplate, True)
	SetTemplateSize(powerUpTemplate, 4.5, 1)
	SetTemplateColors(powerUpTemplate, $FF0000, $FF0000)
	SetTemplateBrightness(powerUpTemplate, 8)
	shellEmitter=CreatePivot()
	;Shell template - Yellow
	shellTemplate = CreateTemplate()
	SetTemplateEmitterBlend(shellTemplate, 3)
	SetTemplateInterval(shellTemplate, 1)
	SetTemplateParticlesPerInterval(shellTemplate, 20)
	SetTemplateEmitterLifeTime(shellTemplate, 1)
	SetTemplateParticleLifeTime(shellTemplate, 70, 85)
	SetTemplateTexture(shellTemplate, "Sprites\spark.dds", 3)
	SetTemplateOffset(shellTemplate, -.2, .2, -.2, .2, -.2, .2)
	SetTemplateVelocity(shellTemplate, -.2, .2, -.2, .4, -.2, .2)
	SetTemplateAlignToFall(shellTemplate, True, 45)
	SetTemplateGravity(shellTemplate, .0015)
	SetTemplateAlphaVel(shellTemplate, True)
	SetTemplateSize(shellTemplate, 4.5, 1)
	SetTemplateColors(shellTemplate, $FFFF00, $FFFF00)
	SetTemplateBrightness(shellTemplate, 8)
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	For ball.balls=Each balls
		If ball<>Null
			gameBallCount=gameBallCount-1
			If ball\entity
				FreeEntity(ball\entity)
				ball\entity = 0
			EndIf
			Delete ball
		EndIf
	Next
	ClearBricks()
	ClearPowerUps()
	gameBoatCount=0
	ClearTrees()
	ClearTikis()
	ClearBullets()
	ClearShells()
	If gameLand<>0
		FreeEntity(gameLand)
		gameLand = 0
	EndIf
	If gameWater<>0
		FreeEntity(gameWater)
		gameWater = 0
	EndIf
	If gameWTex<>0
		FreeTexture(gameWTex)
		gameWTex = 0
	EndIf
	If gameSky<>0
		FreeEntity(gameSky)
		gameSky = 0
	EndIf
	If gameSky2 <> 0
		FreeEntity(gameSky2)
		gameSky2 = 0
	EndIf
	If gameWaterBump<>0
		FreeTexture(gameWaterBump)
		gamewaterBump = 0
	EndIf
	If gameCTex<>0
		FreeTexture(gameCTex)
		gameCTex = 0
	EndIf
	If gameBricks1
		FreeTexture(gameBricks1)
		gameBricks1 = 0
	EndIf
	If gameBricks2
		FreeTexture(gameBricks2)
		gameBricks2 = 0
	EndIf
	If gameBricks3
		FreeTexture(gameBricks3)
		gameBricks3 = 0
	EndIf
	If gameBricks4
		FreeTexture(gameBricks4)
		gameBricks4 = 0
	EndIf
	If gameBricks5
		FreeTexture(gameBricks5)
		gameBricks5 = 0
	EndIf
	If gameBricks6
		FreeTexture(gameBricks6)
		gameBricks6 = 0
	EndIf
	If gameBricks7
		FreeTexture(gameBricks7)
		gameBricks7 = 0
	EndIf
	gameBricks1=LoadTexture("Bricks\blue.dds")
	gameBricks2=LoadTexture("Bricks\purple.dds")
	gameBricks3=LoadTexture("Bricks\red.dds")
	gameBricks4=LoadTexture("Bricks\orange.dds")
	gameBricks5=LoadTexture("Bricks\yellow.dds")
	gameBricks6=LoadTexture("Bricks\green.dds")
	gameBricks7=LoadTexture("Bricks\black.dds")
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	If levelNum < 91
		ball.balls=New balls
		ball\entity=CopyEntity(gameBall)
		ball\main=1
		ball\XSpeed#=1
		ball\YSpeed#=-1
		ball\time%=-1
		ball\timer%=-10
		gameBallCount=gameBallCount+1
		EntityRadius(ball\entity,2)
		EntityType(ball\entity,ballColl)
		PositionEntity(ball\entity,0,0,-50)
		TurnEntity(ball\entity,0,30,0)
		LoadBricks("Levels\"+levelNum+".bmp")
		PositionEntity(gameLightS,50,50,-70)
		TurnEntity(gameLight,0,45,0)
		gameLand = LoadAnimMesh("Levels\land.b3d")
		extModel.EXT_Entity = EXT_InitEntity( gameLand )
		Animate(gameLand, 1, .2)
		gameWater=LoadMesh("Levels\water.b3d");CreatePlane(8)
		gameWTex=LoadTexture("Levels\watertex.dds")
		gameWaterBump=LoadTexture("Levels\waterBump.dds",256)
		gameCTex=LoadTexture("Levels\cube2.bmp",128)
		TextureBlend(gameWaterBump,4)
		;TextureBlend(gameWTex,3)
		ScaleTexture(gameWTex,.25,.25)
		;EntityTexture(gameWater,gameCTex,0,0)
		;EntityTexture(gameWater,gameWaterBump,0,1)
		EntityTexture(gameWater,gameWTex,0,2)
		EntityAlpha(gameWater,.6)
		;EntityFX(gameWater,1)
		PositionEntity(gameCam, 0, 80, -80);-80
		PointEntity(gameCam,gameWater)
		levNum# = 90.0 / 3
		If levelNum < levNum
			gameSky = LoadMesh("Skies\sky1.b3d")
			gameSky2 = LoadMesh("Skies\sky2.b3d")
			EntityAlpha(gameSky, (1-levelNum/levNum))
			EntityAlpha(gameSky2, (levelNum/levNum))
		ElseIf levelNum < (levNum*2)
			gameSky = LoadMesh("Skies\sky2.b3d")
			gameSky2 = LoadMesh("Skies\sky3.b3d")
			EntityAlpha(gameSky, (1.0-levelNum/(levNum*2.0)))
			;FlushKeys()
			;WaitKey()
			EntityAlpha(gameSky2, (levelNum/(levNum*2.0)))
			;FlushKeys()
			;WaitKey()
		Else
			gameSky = LoadMesh("Skies\sky3.b3d")
		EndIf
		EntityOrder(gameSky, 50)
		If gameSky2
			EntityOrder(gameSky2, 50)
		EndIf
		SetTrees(1)
		SetShells(1)
	ElseIf levelNum = 72
		ball.balls=New balls
		ball\entity=CopyEntity(gameBall)
		ball\main=1
		ball\XSpeed#=1
		ball\YSpeed#=-1
		ball\time%=-1
		ball\timer%=-10
		gameBallCount=gameBallCount+1
		EntityRadius(ball\entity,2)
		EntityType(ball\entity,ballColl)
		PositionEntity(ball\entity,0,0,-50)
		TurnEntity(ball\entity,0,30,0)
		LoadBricks("Levels\theend.bmp")
		PositionEntity(gameLightS,50,50,-70)
		TurnEntity(gameLight,0,45,0)
		gameLand=LoadAnimMesh("Levels\land.b3d")
		extModel.EXT_Entity = EXT_InitEntity(gameLand)
		gameWater=LoadMesh("Levels\water.b3d");CreatePlane(8)
		gameWTex=LoadTexture("Levels\watertex.dds")
		gameSky = LoadMesh("Skies\sky3.b3d")
		EntityOrder(gameSky,50)
		gameWaterBump=LoadTexture("Levels\waterBump.dds",256)
		gameCTex=LoadTexture("Levels\cube2.bmp",128)
		;TextureBlend(gameWaterBump,4)
		;TextureBlend(gameWTex,3)
		ScaleTexture(gameWTex,.25,.25)
		;EntityTexture(gameWater,gameCTex,0,0)
		;EntityTexture(gameWater,gameWaterBump,0,1)
		EntityTexture(gameWater,gameWTex,0,2)
		EntityAlpha(gameWater,1)
		;EntityFX(gameWater,1)
		PositionEntity(gameCam,0,80,-80)
		PointEntity(gameCam,gameWater)
		SetTrees(1)
	Else
		ball.balls=New balls
		ball\entity=CopyEntity(gameBall)
		ball\main=1
		ball\XSpeed#=1
		ball\YSpeed#=-1
		ball\time%=-1
		ball\timer%=-10
		gameBallCount=gameBallCount+1
		EntityRadius(ball\entity,2)
		EntityType(ball\entity,ballColl)
		PositionEntity(ball\entity,0,0,-50)
		TurnEntity(ball\entity,0,30,0)
		LoadBricks("Levels\jk.bmp")
		PositionEntity(gameLightS,50,50,-70)
		TurnEntity(gameLight,0,45,0)
		gameLand=LoadAnimMesh("Levels\land.b3d")
		extModel.EXT_Entity = EXT_InitEntity(gameLand)
		gameWater=LoadMesh("Levels\water.b3d");CreatePlane(8)
		gameWTex=LoadTexture("Levels\watertex.dds")
		gameSky = LoadMesh("Skies\sky3.b3d")
		EntityOrder(gameSky,50)
		gameWaterBump=LoadTexture("Levels\waterBump.dds",256)
		gameCTex=LoadTexture("Levels\cube2.bmp",128)
		;TextureBlend(gameWaterBump,4)
		;TextureBlend(gameWTex,3)
		ScaleTexture(gameWTex,.25,.25)
		;EntityTexture(gameWater,gameCTex,0,0)
		;EntityTexture(gameWater,gameWaterBump,0,1)
		EntityTexture(gameWater,gameWTex,0,2)
		EntityAlpha(gameWater,1)
		;EntityFX(gameWater,1)
		PositionEntity(gameCam,0,80,-80)
		PointEntity(gameCam,gameWater)
		SetTrees(1)
	EndIf
	SetTikis((levelNum/90.0))
	lCol = (0+(225*(1-(levelNum/90.0))));31+
	;FlushKeys()
	;WaitKey()
	LightColor(light, lCol, lCol, lCol)
	LightColor(gameLight, 0, 0, 0); lCol, lCol, lCol)
	CreatePirates(gameRoundTime)
	MouseXSpeed()
End Function
;;;;;;;;;;;;;;;;;;;;
Function LoadBricks(file$)
	gameBrickCount=0
	gameRoundTime=0
	bImage=LoadImage(file$)
	bW=ImageWidth(bImage)
	bH=ImageHeight(bImage)
	bW2=bW/2
	bH2=bH/2
	bW=bW-1
	bH=bH-1
	SetBuffer ImageBuffer(bImage)
	LockBuffer ImageBuffer(bImage)
	For y=0 To bH
		For x=0 To bW
			bColor=ReadPixelFast(x,y)
			If bColor=color_Blue
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				brick\hits%=1
				brick\value%=10
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;tempBrick = LoadMesh("Bricks\brick1.b3d")
				;PositionEntity(tempBrick, (x-bW2+.5)*9, 0, (y-bH2)*(-6))
				;TurnEntity(tempBrick, 0, 0, 180)
				;EntityAlpha(tempBrick, .5)
				;EntityParent(tempBrick, brick\entity)
				;
				gameRoundTime=gameRoundTime+20
			ElseIf bColor=color_Purple
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				EntityTexture(FindChild(brick\entity,"Box02"),gameBricks2,0,0)
				FreeTexture(brickTex)
				brick\hits%=2
				brick\value%=40
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;
				gameRoundTime=gameRoundTime+30
			ElseIf bColor=color_Red
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				EntityTexture(FindChild(brick\entity,"Box02"),gameBricks3,0,0)
				FreeTexture(brickTex)
				brick\hits%=3
				brick\value%=80
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;
				gameRoundTime=gameRoundTime+35
			ElseIf bColor=color_Orange
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				EntityTexture(FindChild(brick\entity,"Box02"),gameBricks4,0,0)
				FreeTexture(brickTex)
				brick\hits%=4
				brick\value%=160
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;
				gameRoundTime=gameRoundTime+38
			ElseIf bColor=color_Yellow
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				EntityTexture(FindChild(brick\entity,"Box02"),gameBricks5,0,0)
				FreeTexture(brickTex)
				brick\hits%=5
				brick\value%=320
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;
				gameRoundTime=gameRoundTime+40
			ElseIf bColor=color_Green
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				EntityTexture(FindChild(brick\entity,"Box02"),gameBricks6,0,0)
				FreeTexture(brickTex)
				brick\hits%=6
				brick\value%=640
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;
				gameRoundTime=gameRoundTime+41
			ElseIf bColor=color_Black
				brick.bricks=New bricks
				gameBrickCount=gameBrickCount+1
				brick\entity=LoadAnimMesh("Bricks\brick1.b3d")
				EntityTexture(FindChild(brick\entity,"Box02"),gameBricks7,0,0)
				FreeTexture(brickTex)
				brick\hits%=7
				brick\value%=1280
				EntityType(FindChild(brick\entity,"Box02"),brickColl)
				EntityType(FindChild(brick\entity,"Box01"),brickHColl)
				PositionEntity(brick\entity,(x-bW2+.5)*9,0,(y-bH2)*(-6))
				;
				gameRoundTime = gameRoundTime + 50;42
			EndIf
		Next
	Next
	If gameRoundTime < 900
		gameRoundTime = gameRoundTime + 900
	EndIf
	UnlockBuffer ImageBuffer(bImage)
	SetBuffer BackBuffer()
	FreeImage(bImage)
End Function
;;;;;;;;;;;;;;;;;;;;
Function ClearBricks()
	For brick.bricks=Each bricks
		FreeEntity(brick\entity)
		Delete brick
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function ClearPowerUps()
	For pUp.powerUp=Each powerUp
		If pUp\sprite
			FreeEntity(pUp\sprite)
			pUp\sprite = 0
		EndIf
		If pUp\entity
			FreeEntity(pUp\entity)
			pUp\entity = 0
		EndIf
		If pUp\pType=5
			gameCannonsActive=0
		ElseIf pUp\pType=6
			gameBoatCount=gameBoatCount-1
		EndIf
		Delete pUp
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function SetTrees(choice%)
	Select choice
		Case 1
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-30,15,90)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-85,15,80)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-86,15,52)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-89,15,0)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,50,15,90)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,85,15,80)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,90,15,40)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,89,15,0)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		Default
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-30,15,90)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-85,15,80)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-86,15,52)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,-89,15,0)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,50,15,90)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,85,15,80)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,86,15,40)
			Animate(tree\entity,1,.1)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.1)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
			tree.trees=New trees
			tree\entity=LoadAnimMesh("Levels\tree.b3d")
			PositionEntity(tree\entity,89,15,0)
			Animate(tree\entity,1,.2)
			Animate(FindChild(tree\entity,"Cylinder01"),1,.2)
			;ScaleEntity(tree\entity,.22,.22,.22)
			;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	End Select
End Function
;;;;;;;;;;;;;;;;;;;;
Function ClearTrees()
	For tree.trees=Each trees
		If tree\entity
			FreeEntity(tree\entity)
		EndIf
		Delete tree
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function SetTikis(percentage#)
	tiki.tikis = New tikis
	tiki\entity = LoadAnimMesh("Levels\tiki.b3d")
	tiki\light = CreateLight(2, tiki\entity)
	tiki\sprite = LoadSprite("Sprites\red.dds", 1, tiki\light)
	PositionEntity(tiki\entity, -89, 15, 25)
	ScaleEntity(tiki\entity, .05, .05, .05)
	LightColor(tiki\light, (percentage*255), (percentage*127), 0)
	LightRange(tiki\light, 20)
	PositionEntity(tiki\light, 0, 450, 0)
	ScaleSprite(tiki\sprite, (15*percentage), (15*percentage))
	;;;;;
	tiki.tikis = New tikis
	tiki\entity = LoadAnimMesh("Levels\tiki.b3d")
	tiki\light = CreateLight(2, tiki\entity)
	tiki\sprite = LoadSprite("Sprites\red.dds", 1, tiki\light)
	PositionEntity(tiki\entity, 89, 15, 25)
	ScaleEntity(tiki\entity, .05, .05, .05)
	LightColor(tiki\light, (percentage*255), (percentage*127), 0)
	LightRange(tiki\light, 20)
	PositionEntity(tiki\light, 0, 450, 0)
	ScaleSprite(tiki\sprite, (15*percentage), (15*percentage))
	;;;;;
	tiki.tikis = New tikis
	tiki\entity = LoadAnimMesh("Levels\tiki.b3d")
	tiki\light = CreateLight(2, tiki\entity)
	tiki\sprite = LoadSprite("Sprites\red.dds", 1, tiki\light)
	PositionEntity(tiki\entity, -57, 15, 90)
	ScaleEntity(tiki\entity, .05, .05, .05)
	LightColor(tiki\light, (percentage*255), (percentage*127), 0)
	LightRange(tiki\light, 20)
	PositionEntity(tiki\light, 0, 450, 0)
	ScaleSprite(tiki\sprite, (15*percentage), (15*percentage))
	;;;;;
	tiki.tikis = New tikis
	tiki\entity = LoadAnimMesh("Levels\tiki.b3d")
	tiki\light = CreateLight(2, tiki\entity)
	tiki\sprite = LoadSprite("Sprites\red.dds", 1, tiki\light)
	PositionEntity(tiki\entity, 28, 15, 90)
	ScaleEntity(tiki\entity, .05, .05, .05)
	LightColor(tiki\light, (percentage*255), (percentage*127), 0)
	LightRange(tiki\light, 20)
	PositionEntity(tiki\light, 0, 450, 0)
	ScaleSprite(tiki\sprite, (15*percentage), (15*percentage))
End Function
;;;;;;;;;;;;;;;;;;;;
Function ClearTikis()
	For tiki.tikis = Each tikis
		If tiki\light
			FreeEntity(tiki\light)
			tiki\light = 0
		EndIf
		If tiki\entity
			FreeEntity(tiki\entity)
			tiki\entity = 0
		EndIf
		Delete tiki
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function ClearBullets()
	For bullet.bullets=Each bullets
		If bullet\entity
			FreeEntity(bullet\entity)
		EndIf
		Delete bullet
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function PauseGame()
	pauseImage=CreateImage(gW,gH)
	GrabImage(pauseImage,0,0)
	powerUpImage = LoadImage("MenuObjects\PowerUpChart.bmp")
	Repeat
	
	;UpdateWorld()
	;RenderWorld()
	gMX=MouseX()
	gMY=MouseY()
	DrawImage(pauseImage,0,0)
	DrawImage(powerUpImage, 338, 85)
	DrawImage(gameMouse,gMX,gMY)
	jf_text(gameFont,gW2+128,gH2,"Paused",1,1)
	WaitTimer(gameTimer)
	Flip
	If KeyHit(pause_key)
		Exit
	EndIf
	Forever
	FreeImage(powerUpImage)
	FreeImage(pauseImage)
	pauseImage=0
	gamePause=0
	FlushKeys()
	FlushMouse()
End Function
;;;;;;;;;;;;;;;;;;;;
Function ClearShells()
	For shell.shells=Each shells
		If shell\entity
			FreeEntity(shell\entity)
		EndIf
		Delete shell
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function FreeGame()
	;
	If ChannelPlaying(gameChnlMusic)
		StopChannel(gameChnlMusic)
	EndIf
	If ChannelPlaying(gameChnlAmbient)
		StopChannel(gameChnlAmbient)
	EndIf
	;
	If l_texture
		FreeTexture(l_texture)
		l_texture = 0
	EndIf
	If l_surface
		FreeEntity(l_surface)
		l_surface = 0
	EndIf
	;
	ClearBricks()
	ClearPowerUps()
	ClearTrees()
	ClearTikis()
	ClearBullets()
	ClearShells()
	For x=1 To 100
		UpdateParticles()
	Next
	If brickTemplate
		FreeTemplate(brickTemplate)
		brickTemplate = 0
	EndIf
	If treasureTemplate
		FreeTemplate(treasureTemplate)
		treasureTemplate = 0
	EndIf
	If powerUpTemplate
		FreeTemplate(powerUpTemplate)
		powerUpTemplate = 0
	EndIf
	FreeEmitters()
	FreeParticles()
	FreeParrots()
	FreePirates()
	;FreeEntity(brickEmitter)
	;FreeEntity(powerUpEmitter)
	;FreeEntity(shellEmitter)
	;brickEmitter=0
	;powerUpEmitter=0
	;shellEmitter=0
	;jf_free_font(gameFont)
	If gameBall
		FreeEntity(gameBall)
		gameBall=0
	EndIf
	If gamePlayer
		FreeEntity(gamePlayer)
		gamePlayer=0
	EndIf
	If gameTurtle
		FreeEntity(gameTurtle)
		gameTurtle=0
	EndIf
	For ball.balls=Each balls
		FreeEntity(ball\entity)
		Delete ball
	Next
	If gameX
		FreeEntity(gameX)
		gameX = 0
	EndIf
	If gameLand<>0
		FreeEntity(gameLand)
		gameLand = 0
	EndIf
	If gameWater<>0
		FreeEntity(gameWater)
		gameWater = 0
	EndIf
	If gameWTex<>0
		FreeTexture(gameWTex)
		gameWTex = 0
	EndIf
	If gameSky<>0
		FreeEntity(gameSky)
		gameSky = 0
	EndIf
	If gameSky2 <> 0
		FreeEntity(gameSky2)
		gameSky2 = 0
	EndIf
	If gameWaterBump<>0
		FreeTexture(gameWaterBump)
		gamewaterBump = 0
	EndIf
	If gameCTex<>0
		FreeTexture(gameCTex)
		gameCTex = 0
	EndIf
	If gameBricks1
		FreeTexture(gameBricks1)
		gameBricks1 = 0
	EndIf
	If gameBricks2
		FreeTexture(gameBricks2)
		gameBricks2 = 0
	EndIf
	If gameBricks3
		FreeTexture(gameBricks3)
		gameBricks3 = 0
	EndIf
	If gameBricks4
		FreeTexture(gameBricks4)
		gameBricks4 = 0
	EndIf
	If gameBricks5
		FreeTexture(gameBricks5)
		gameBricks5 = 0
	EndIf
	If gameBricks6
		FreeTexture(gameBricks6)
		gameBricks6 = 0
	EndIf
	If gameBricks7
		FreeTexture(gameBricks7)
		gameBricks7 = 0
	EndIf
	LightColor(light, 255, 255, 201)
	gameBallCount=0
End Function
;;;;;;;;;;;;;;;;;;;;
Function SetShells(sChoice%)
	Select sChoice%
		Case 1
			For x=1 To 10
			For y=-55 To 70 Step 125
				sRandNum=Rand(1,100)
				If sRandNum>90
					shell.shells=New shells
					shell\entity=LoadMesh("Bricks\shell.b3d")
					EntityRadius(shell\entity,4,1)
					EntityType(shell\entity,shellColl)
					PositionEntity(shell\entity,(x*14)-77,0,y)
					ScaleEntity(shell\entity,.07,.07,.07)
				EndIf
			Next
			Next
		Default
			For x=1 To 10
				sRandNum=Rand(1,100)
				If sRandNum>90
					shell.shells=New shells
					shell\entity=LoadMesh("Bricks\shell.b3d")
					EntityRadius(shell\entity,4,1)
					EntityType(shell\entity,shellColl)
					PositionEntity(shell\entity,(x*12)-77,0,y)
					ScaleEntity(shell\entity,.08,.08,.08)
				EndIf
			Next
	End Select
End Function
;;;;;;;;;;;;;;;;;;;;
Function CreatePirates(time#)
	FreePirates()
	;cube = CreateCube()
	;ScaleEntity(cube, 1, 1, 59.5)
	;PositionEntity(cube, 80, 21, 24.5)
	pirate.pirates=New pirates
	pirate\entity=LoadAnimMesh("Bricks\pirate.b3d")
	pirate\side = 1
	pirate\distTraveled#=0
	pirate\distTurned# = 0
	pirate\travelSpeed# = 190.0 / time;gameRoundTime
	aSpeed# = pirate\travelSpeed * 6
	;FlushKeys()
	;WaitKey()
	ExtractAnimSeq(pirate\entity, 0, 29)
	ExtractAnimSeq(pirate\entity, 30, 147)
	ExtractAnimSeq(pirate\entity, 148, 190)
	ExtractAnimSeq(pirate\entity, 191, 220)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 0, 29)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 30, 147)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 148, 190)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 191, 220)
	Animate(pirate\entity, 1, aSpeed, 1)
	Animate(FindChild(pirate\entity,"Box01"), 1, aSpeed, 1)
	EntityColor(pirate\entity,0,0,255)
	PositionEntity(pirate\entity, -80, 20, -35)
	ScaleEntity(pirate\entity, .1, .1, .1)
	;;;;;
	pirate.pirates=New pirates
	pirate\entity = LoadAnimMesh("Bricks\pirate.b3d")
	pirate\side = 2
	pirate\distTraveled#=0
	pirate\distTurned# = 0
	pirate\travelSpeed# = 190.0 / time * 3.0;gameRoundTime * 3.0
	pirate\travelDist = 190.0 / gameBrickCount
	aSpeed# = pirate\travelSpeed * 6
	ExtractAnimSeq(pirate\entity, 0, 29)
	ExtractAnimSeq(pirate\entity, 30, 147)
	ExtractAnimSeq(pirate\entity, 148, 190)
	ExtractAnimSeq(pirate\entity, 191, 220)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 0, 29)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 30, 147)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 148, 190)
	ExtractAnimSeq(FindChild(pirate\entity,"Box01"), 191, 220)
	Animate(pirate\entity, 1, aSpeed, 1)
	Animate(FindChild(pirate\entity,"Box01"), 1, aSpeed, 1)
	EntityColor(pirate\entity, 255, 0, 0)
	PositionEntity(pirate\entity, 80, 20, -35)
	ScaleEntity(pirate\entity, .1, .1, .1)
	EntityColor(pirate\entity, 255, 0, 0)
End Function
;;;;;;;;;;;;;;;;;;;;
Function UpdatePirates(uMode = 1)
If uMode = 1
	For pirate.pirates=Each pirates
		aSpeed# = pirate\travelSpeed * 6
		If pirate\side = 1
			If pirate\distTraveled < 190
				MoveEntity(pirate\entity, 0, 0, pirate\travelSpeed)
				pirate\distTraveled = pirate\distTraveled + pirate\travelSpeed
				If pirate\distTraveled >= 119
					If pirate\distTraveled <= 121
						TurnEntity(pirate\entity, 0, -(90.0/(2.0/pirate\travelSpeed)), 0)
					EndIf
				EndIf
			Else
				aSeq = AnimSeq(pirate\entity)
				If aSeq = 1
					Animate(pirate\entity, 3, .25, 2)
					Animate(FindChild(pirate\entity,"Box01"), 3, .25, 2)
				ElseIf Not Animating(pirate\entity)
					Animate(pirate\entity, 1, .25, 3)
					Animate(FindChild(pirate\entity,"Box01"), 1, .25, 3)
				EndIf
			EndIf
		Else
			If pirate\distTraveled < 190
				If pirate\destDist > pirate\distTraveled
					If (Not Animating(pirate\entity)) Or AnimSeq(pirate\entity) <> 1
						Animate(pirate\entity, 1, aSpeed, 1, 1)
						Animate(FindChild(pirate\entity,"Box01"), 1, aSpeed, 1, 1)
					EndIf
					MoveEntity(pirate\entity, 0, 0, pirate\travelSpeed)
					pirate\distTraveled = pirate\distTraveled + pirate\travelSpeed
					If pirate\distTraveled >= 119
						If pirate\distTraveled <= 124
							If pirate\distTurned < 100
								If pirate\distTurned + (100.0/(2.0/pirate\travelSpeed)) > 100
									pirate\distTurned = 100
									RotateEntity(pirate\entity, 0, 100, 0)
								Else
									pirate\distTurned = pirate\distTurned + (100.0/(2.0/pirate\travelSpeed))
									TurnEntity(pirate\entity, 0, (100.0/(2.0/pirate\travelSpeed)), 0)
								EndIf
							EndIf
						EndIf
					EndIf
				ElseIf AnimSeq(pirate\entity) = 1
					Animate(pirate\entity, 3, .25, 3)
					Animate(FindChild(pirate\entity,"Box01"), 3, .25, 3)
				EndIf
			Else
				aSeq = AnimSeq(pirate\entity)
				If aSeq = 1
					Animate(pirate\entity, 3, 1, 2)
					Animate(FindChild(pirate\entity,"Box01"), 3, 1, 2)
				ElseIf Not Animating(pirate\entity)
					Animate(pirate\entity, 1, .5, 3)
					Animate(FindChild(pirate\entity,"Box01"), 1, .5, 3)
				EndIf
			EndIf
		EndIf
	Next
Else
	For pirate.pirates = Each pirates
		If pirate\side = 1
			If Animating(pirate\entity)
				Animate(pirate\entity, 0)
				Animate(FindChild(pirate\entity,"Box01"), 0)
			EndIf
		ElseIf pirate\side = 2
			If pirate\distTraveled < 190
				If pirate\destDist > pirate\distTraveled
					If (Not Animating(pirate\entity)) Or AnimSeq(pirate\entity) <> 1
						Animate(pirate\entity, 1, aSpeed, 1, 1)
						Animate(FindChild(pirate\entity,"Box01"), 1, aSpeed, 1, 1)
					EndIf
					MoveEntity(pirate\entity, 0, 0, pirate\travelSpeed*2)
					pirate\distTraveled = pirate\distTraveled + (pirate\travelSpeed*2)
					If pirate\distTraveled >= 119
						If pirate\distTraveled <= 121
							If pirate\distTurned + (100.0/(2.0/pirate\travelSpeed)) > 100
								pirate\distTurned = 100
								RotateEntity(pirate\entity, 0, 100, 0)
								;TurnEntity(pirate\entity, 0, 100 - pirate\distTurned, 0)
							Else
								pirate\distTurned = pirate\distTurned + ((100.0/(2.0/pirate\travelSpeed))*2)
								TurnEntity(pirate\entity, 0, (100.0/(2.0/pirate\travelSpeed))*2, 0)
							EndIf
						EndIf
					EndIf
				ElseIf AnimSeq(pirate\entity) = 1
					Animate(pirate\entity, 3, .25, 3)
					Animate(FindChild(pirate\entity,"Box01"), 3, .25, 3)
				EndIf
			Else
				aSeq = AnimSeq(pirate\entity)
				If aSeq = 1
					Animate(pirate\entity, 3, 2, 2)
					Animate(FindChild(pirate\entity,"Box01"), 3, 2, 2)
				ElseIf Not Animating(pirate\entity)
					activateChest = 1
					Animate(pirate\entity, 1, .25, 3)
					Animate(FindChild(pirate\entity,"Box01"), 1, .25, 3)
				EndIf
			EndIf
		EndIf
	Next
EndIf
End Function
;;;;;;;;;;;;;;;;;;;;
Function FreePirates()
	For pirate.pirates=Each pirates
		FreeEntity(pirate\entity)
		Delete pirate
	Next
End Function
;;;;;;;;;;;;;;;;;;;;
Function UpdateParrots()

parrotTimer = parrotTimer + Rand(1, 3)
If parrotTimer > parrotTime
	parrotTimer = 0
	parrot.parrots = New parrots
	parrot\entity = LoadAnimMesh("Bricks\parrot.b3d")
	parrot\timer = 0
	parrot\time = 200
	parrot\speed = 1
	randNum = Rand(1, 2)
	Animate(parrot\entity, 1, 1)
	If randNum = 1
		PositionEntity(parrot\entity, -100, 30, Rnd(-100, 100))
		TurnEntity(parrot\entity, 0, Rnd(0, -179), 0)
	Else
		PositionEntity(parrot\entity, 100, 30, Rnd(-100, 100))
		TurnEntity(parrot\entity, 0, Rnd(0, 179), 0)
	EndIf
	ScaleEntity(parrot\entity, Rnd(.04,.05), Rnd(.04,.05), Rnd(.04,.05))
EndIf
For parrot.parrots = Each parrots
	MoveEntity(parrot\entity, 0, 0, parrot\speed)
	parrot\timer = parrot\timer + 1
	If parrot\timer = Rand(parrot\time)
		rNum = Rand(1, 2)
		If rNum = 1
			EmitSound(soundSquak1, parrot\entity)
		Else
			EmitSound(soundSquak2, parrot\entity)
		EndIf
	EndIf
	If parrot\timer > parrot\time
		If parrot\entity
			FreeEntity(parrot\entity)
			parrot\entity = 0
		EndIf
		Delete parrot
	EndIf
Next

End Function
;;;;;;;;;;;;;;;;;;;;
Function FreeParrots()

For parrot.parrots = Each parrots
	If parrot\entity
		FreeEntity(parrot\entity)
		parrot\entity = 0
	EndIf
	Delete parrot
Next

End Function
;;;;;;;;;;;;;;;;;;;;
Function ChooseFile$()
	PlaySound(soundLoad1)
	Local mousedot=LoadImage("MenuObjects\pointer.bmp");CreateImage(16,16)
	Local leafImage=LoadImage("MenuObjects\leaf64.bmp")
	Local backTex=LoadImage("Levels\watertex.jpg")
	Local oMOpen=LoadImage("MenuObjects\oMOpen.bmp")
	Local oMCancel=LoadImage("MenuObjects\oMCancel.bmp")
	Local oMXdelete=LoadImage("MenuObjects\oMXdelete.bmp")
	Local loadGImage=LoadImage("MenuObjects\loadGame.bmp")
	Local dWarning = LoadImage("MenuObjects\dWarning.bmp")
	Local sYes = LoadImage("MenuObjects\sYes.bmp")
	Local sNo = LoadImage("MenuObjects\sNo.bmp")
	HandleImage(loadGImage,249,0)
	;SetBuffer ImageBuffer(mousedot)
	;Color(255,255,0)
	;Rect(0,0,16,16)
	;Color(0,0,0)
	;SetBuffer BackBuffer()
	oMX=gW2-256
	oMY=gH2-256+100;+100
	oMOX=oMX+68;68, 445
	oMOY=oMY+445
	oMCX=oMX+301;301, 445
	oMCY=oMY+445
	oMXX=oMx+217;217, 419
	oMXY=oMY+419
	oMOD=0
	oMCD=0
	oMXD=0
	upX=oMX+485
	upY=oMY+35
	downX=oMX+485
	downY=oMY+392
	sliderX=oMX+486
	sliderY#=oMY+49
	sliderSY=oMY+49
	sliderMY=oMY+352
	xDelete = 0
	close=0
	FlushKeys()
	FlushMouse()
	currentDirectory$=CurrentDir$()+"\Saves\"
	;;;;;;;;;;;;;;;;;;;
	.label2
	;;;;;
	myDir=0
	myDir=ReadDir(currentDirectory$)
	ending$=""
	If myDir<>0
	fCount=0
	Repeat
	
	
	dFile$=NextFile(myDir)
	If dFile=""
		Exit
	EndIf
	ending$=Right$(dFile$,3)
	ending$=Lower$(ending$)
	If ending$="sbg"
		fCount=fCount+1
	EndIf
	
	Forever
	CloseDir(myDir)
	EndIf
	sliderY=sliderSY
	sChange#=303.00000/(fCount-24)
	myDir=0
	myDir=ReadDir(currentDirectory$)
	ending$=""
	If myDir<>0
	Dim dFiles$(fCount)
	number=0

	Repeat

	dFile$=NextFile(myDir)
	If dFile=""
		Exit
	EndIf
	
	ending$=Right$(dFile$,3)
	ending$=Lower$(ending$)
	If ending$="sbg"
	dFiles$(number)=dFile$
	
	number=number+1
	EndIf
	
	Forever
	;;;;;;;;;
	CloseDir(myDir)
	EndIf
	;;;;;;;;;;;;;;;;;;;
	okX=oMX+68
	okY=oMY+445
	cancelX=oMX+301
	cancelY=oMY+445
	xdeleteX=oMX+217
	xdeleteY=oMY+419
	fCount2 = 0
	Repeat
	Cls

	mX=MouseX()
	mY=MouseY()
	
	If KeyHit(1)
		close=3
	ElseIf KeyHit(28)
		close=1
	EndIf
	
	;UpdateWorld()
	;RenderWorld()
	;DrawMenu(1)
	bTX=bTX+1
	bTY=bTY-1
	TileImage(backTex,bTX,bTY)
	DrawImage(gameOpenMenuB,oMX,oMY)
	DrawImage(gameOpenMenu,oMX,oMY)
	DrawImage(gameFileSlider,sliderX,sliderY)
	If oMOD=1
		DrawImage(oMOpen,oMOX,oMOY)
	ElseIf oMCD=1
		DrawImage(oMCancel,oMCX,oMCY)
	ElseIf oMXD=1
		DrawImage(oMXdelete,oMXX,oMXY)
	EndIf
	If xDelete <> 1
	If KeyDown(2)
		oMOD=1
		oMCD=0
		oMXD=0
	ElseIf KeyDown(3)
		oMOD=0
		oMCD=1
		oMXD=0
	ElseIf KeyDown(4)
		oMOD=0
		oMCD=0
		oMXD=1
	Else
		;oMOD=0
		;oMCD=0
		;oMXD=0
	EndIf
	Color(0,0,0)
	;FlushKeys()
	;WaitKey()
	For x = 0 To fCount - 1
		If x<(11+fCount2) And x >= fCount2 - 1
		fileX=oMX+5
		fileY=oMy+40+(32*x)-(32*fCount2);+20
		If selectedFile$=currentDirectory+dFiles(x)
			Color(132,176,223)
			Rect(fileX,fileY,480,32)
			Color(7,7,7)
		EndIf
		DrawImage(leafImage,fileX,fileY)
		locLength=Len(dFiles(x))
		locLength=locLength-8
		locFName$=dFiles(x)
		If Lower(Right$(locFName, 3)) = "sbg"
		locFName$=Left$(locFName$,locLength)
		jf_text(gameFont,128+fileX,fileY,locFName$);Text(128+fileX,fileY,locFName$);dFiles(x))
		If ImageRectOverlap(mouseDot,mX,mY,fileX,fileY,480,32)
			If clickTime<1
				If MouseHit(1)
					selectFile$=dFiles(x)
					selectedFile$=currentDirectory+dFiles(x)
					previousFile$=selectedFile$
					clickTime=30
				EndIf
			Else
				If MouseHit(1)
					;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
					selectedFile$=currentDirectory+dFiles(x)
					If selectedFile$=previousFile$;<>""
						typeFile=FileType(selectedFile)
						;WaitKey()
						If typeFile=1
							locFName$=""
							locLength=Len(dFiles(x))
							locLength=locLength-8
							locFName$=dFiles(x)
							locFName$=Left$(locFName$,locLength)
							gamePName$=locFName$
							gameIName=jf_create_text(gameFont,127,63,gamePName,1,1)
							close=1
						ElseIf typeFile=2
							currentDirectory$=selectedFile
							If Instr(currentDirectory,"..")
								length=Len(currentDirectory)
								length=length-3
								currentDirectory=Left(currentDirectory,length)
								Repeat
									If Instr(currentDirectory,"\",Len(currentDirectory)-1)
										currentDirectory=Left(currentDirectory,Len(currentDirectory)-1)
										Exit
									EndIf
									currentDirectory=Left(currentDirectory,Len(currentDirectory)-1)
								Forever
							EndIf
							If Not Instr(currentDirectory,"\",Len(currentDirectory)-1)
								currentDirectory=currentDirectory+"\"
							EndIf
							;;;;;;;;;;;;;;;;;;;
							myDir=0
							myDir=ReadDir(currentDirectory$)
							If myDir<>0
							fCount=0
							Repeat
							
							dFile$=NextFile(myDir)
							If dFile=""
								Exit
							EndIf
							fCount=fCount+1
							
							
							Forever
							CloseDir(myDir)
							EndIf
							sliderY=sliderSY
							sChange#=303.00000/(fCount-24)
							myDir=0
							myDir=ReadDir(currentDirectory$)
							If myDir<>0
							Dim dFiles$(fCount)
							number=0
						
							Repeat
							
							dFile$=NextFile(myDir)
							If dFile=""
								Exit
							EndIf
							
							dFiles$(number)=dFile$
							
							number=number+1
							
							Forever
							;;;;;;;;;
							CloseDir(myDir)
							EndIf
							;;;;;;;;;;;;;;;;;;;
						EndIf
					EndIf
					;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				EndIf
			EndIf
		EndIf
		EndIf
		EndIf
	Next
	clickTime=clickTime-1
	If clickTime<0
		clickTime=0
	EndIf
	If RectsOverlap(mX,mY,1,1,okX,okY,134,37)
		oMOD=1
		oMCD=0
		oMXD=0
	ElseIf RectsOverlap(mX,mY,1,1,cancelX,cancelY,134,37)
		oMOD=0
		oMCD=1
		oMXD=0
	ElseIf RectsOverlap(mX,mY,1,1,xdeleteX,xdeleteY,69,70)
		oMOD=0
		oMCD=0
		oMXD=1
	Else
		oMOD=0
		oMCD=0
		oMXD=0
	EndIf
	If MouseDown(1)
		If sliding <> 1
			If ImageRectOverlap(mouseDot,mX,mY,okX,okY,134,37)
				;WaitKey()
				If selectedFile$<>""
					typeFile=FileType(selectedFile)
					If typeFile=1
						locFName$=""
						locLength=Len(selectFile$)
						locLength=locLength-8
						locFName$=selectFile$
						locFName$=Left$(locFName$,locLength)
						gamePName$=locFName$
						gameIName=jf_create_text(gameFont,127,63,gamePName,1,1)
						;WaitKey()
						close=1
					ElseIf typeFile=2
						currentDirectory$=selectedFile
						If Instr(currentDirectory,"..")
							length=Len(currentDirectory)
							length=length-3
							currentDirectory=Left(currentDirectory,length)
							Repeat
							If Instr(currentDirectory,"\",Len(currentDirectory)-1)
								currentDirectory=Left(currentDirectory,Len(currentDirectory)-1)
								Exit
							EndIf
							currentDirectory=Left(currentDirectory,Len(currentDirectory)-1)
						Forever
						EndIf
						;;;;;;;;;;;;;;;;;;;
						myDir=0
						myDir=ReadDir(currentDirectory$)
						If myDir<>0
						fCount=0
						Repeat
						
						dFile$=NextFile(myDir)
						If dFile=""
							Exit
						EndIf
						fCount=fCount+1
						
						Forever
						CloseDir(myDir)
						EndIf
						sliderY=sliderSY
						sChange#=303.00000/(fCount-24)
						myDir=0
						myDir=ReadDir(currentDirectory$)
						If myDir<>0
						Dim dFiles$(fCount)
						number=0
					
						Repeat
						
						dFile$=NextFile(myDir)
						If dFile=""
							Exit
						EndIf
						
						dFiles$(number)=dFile$
						
						number=number+1
						
						Forever
						;;;;;;;;;
						CloseDir(myDir)
						EndIf
						;;;;;;;;;;;;;;;;;;;
					EndIf
				EndIf
			ElseIf RectsOverlap(mX,mY,1,1,xdeleteX,xdeleteY,69,70)
				If selectedFile <> ""
					xDelete = 1
				EndIf
			ElseIf ImageRectOverlap(mouseDot,mX,mY,cancelX,cancelY,134,37)
				close=3
			ElseIf ImageRectOverlap(mouseDot,mX,mY,upX,upY,23,14)
				If fCount>23
					clickTime2=clickTime2+1
					If clickTime2<2
						fCount2=fCount2-1
						sliderY#=sliderY-sChange
					ElseIf clickTime2>20
						fCount2=fCount2-1
						sliderY#=sliderY-sChange
					EndIf
					If sliderY<sliderSY
						sliderY=sliderSY
					EndIf
					If fCount2<0
						fCount2=0
					EndIf
				EndIf
			ElseIf ImageRectOverlap(mouseDot,mX,mY,downX,downY+38,23,1)
				If fCount>23
					clickTime2=clickTime2+1
					If clickTime2<2
						fCount2=fCount2+1
						sliderY=sliderY+sChange
					ElseIf clickTime2>20
						fCount2=fCount2+1
						sliderY=sliderY+sChange
					EndIf
					If sliderY>sliderMY
						sliderY=sliderMY
					EndIf
					If fCount2>fCount-12
						fCount2=fCount-12
					EndIf
				EndIf
			ElseIf ImagesOverlap(mouseDot,mX,mY,gameFileSlider,sliderX,sliderY)
				If fCount > 23
					sliding=1
				EndIf
			EndIf
		EndIf
	Else
		sliding=0
		clickTime2=0
	EndIf
	If sliding=1
		If fCount>23
			mYS=MouseYSpeed()
			;MoveMouse(sliderX+10,sliderY+20)
			MouseYSpeed()
			sChange2 = mYS; / 10
			If mYS<0
				;fCount2=fCount2-1
				sliderY = mY;(sliderY+sChange2);-sChange
			ElseIf mYS>0
				;fCount2=fCount2+1
				sliderY = mY;sliderY+sChange2;+sChange
			EndIf
		EndIf
		If sliderY<sliderSY
			sliderY=sliderSY
		ElseIf sliderY>sliderMY
			sliderY=sliderMY
		EndIf
		fCount2 = (sliderY - sliderSY) / 330.0 * fCount + .5
		If fCount2<0
			fCount2=0
		ElseIf fCount2>fCount-12
			fCount2=fCount-12
		EndIf
	EndIf
	mZS=MouseZSpeed()
	If mZS<0
		If fCount>23
			clickTime2=clickTime2+1
			If clickTime2<2
				fCount2=fCount2+1
				sliderY=sliderY+sChange
			ElseIf clickTime2>20
				fCount2=fCount2+1
				sliderY=sliderY+sChange
			EndIf
			If sliderY>sliderMY
				sliderY=sliderMY
			EndIf
			If fCount2>fCount-12
				fCount2=fCount-12
			EndIf
		EndIf
	ElseIf mZS>0
		If fCount>23
			clickTime2=clickTime2+1
			If clickTime2<2
				fCount2=fCount2-1
				sliderY=sliderY-sChange
			ElseIf clickTime2>20
				fCount2=fCount2-1
				sliderY=sliderY-sChange
			EndIf
			If sliderY<sliderSY
				sliderY=sliderSY
			EndIf
			If fCount2<0
				fCount2=0
			EndIf
		EndIf
	EndIf
	FlushMouse()
	EndIf
	;Color(255,255,255)
	;Text(0,40,"clickTime2: "+clickTime2);"selectedFile: "+selectedFile)
	;Text(0,60,"fCount: "+fCount)
	;Text(0,80,"fCount2: "+fCount2)
	;Text(0,100,"sChange: "+sChange)
	;Text(0,120,"formula: "+(303.00000/fCount))
	;Text(0,140,"sliderY: "+sliderY)
	;Text(0,180,"(fCount-2)*sChange: "+((fCount-2)*sChange))
	;Text(0,200,"mYS: "+mYS)
	;Text(0,220,"mZS: "+mZS)
	DrawImage(loadGImage,gW2,63)
	If xDelete = 1
		DrawImage(dWarning, 255, 300)
		If MouseHit(1)
			If RectsOverlap(mX, mY, 1, 1, 320, 474, 145, 47)
				DeleteFile(selectedFile)
				xDelete = 0
				Goto label2
			ElseIf RectsOverlap(mX, mY, 1, 1, 561, 476, 145, 47)
				xDelete = 0
			EndIf
		Else
			If RectsOverlap(mX, mY, 1, 1, 320, 474, 145, 47)
				DrawImage(sYes, 320, 474)
			ElseIf RectsOverlap(mX, mY, 1, 1, 561, 476, 145, 47)
				DrawImage(sNo, 561, 476)
			EndIf
		EndIf
	EndIf

	DrawImage(mousedot,mx,my)
	WaitTimer(gameTimer)
	Flip
	Until close
	FlushKeys()
	FlushMouse()
	FreeImage(oMOpen)
	FreeImage(oMCancel)
	FreeImage(oMXdelete)
	FreeImage(loadGImage)
	FreeImage(mouseDot)
	FreeImage(leafImage)
	FreeImage(backTex)
	FreeImage(dWarning)
	FreeImage(sYes)
	FreeImage(sNo)
	If close=1
		Return(selectedFile$)
	Else
		Return(3)
	EndIf
End Function
;;;;;;;;;;;;;;;;;;;;
Function PlayMenu%()
close=0
Local backTex=LoadImage("Levels\watertex.jpg")
Local mouseDot=LoadImage("MenuObjects\pointer.bmp")
Local buttonNew=LoadAnimImage("MenuObjects\newGameA.bmp",500,128,0,2)
Local buttonLoad=LoadAnimImage("MenuObjects\loadGameA.bmp",500,128,0,2)
Local buttonCancel=LoadAnimImage("MenuObjects\cancelA.bmp",250,128,0,2)
HandleImage(buttonCancel,127,0)
bNX=5
bLX=517
bCX=511
bCC=383
bYs=255
bCY=511
bNF=0
bLF=0
bCF=0
Local mX,mY
Local mHit=0
Repeat
Cls

mX=MouseX()
mY=MouseY()
bTX=bTX+1
bTY=bTY-1
;TurnEntity(pivot,0,.2,0)
;UpdateWorld()
;l_update();RenderWorld()
TileImage(backTex,bTX,bTY)
DrawImage(buttonNew,bNX,bYs,bNF)
DrawImage(buttonLoad,bLX,bYs,bLF)
DrawImage(buttonCancel,bCX,bCY,bCF)
DrawImage(mouseDot,mX,mY)
mHit=MouseHit(1)
If RectsOverlap(mX,mY,1,1,bNX,bYs,500,128)
	If mHit
		close=1
	EndIf
	bNF=1
	bLF=0
	bCF=0
ElseIf RectsOverlap(mX,mY,1,1,bLX,bYs,500,128)
	If mHit
		close=2
	EndIf
	bNF=0
	bLF=1
	bCF=0
ElseIf RectsOverlap(mX,mY,1,1,bCC,bCY,250,128)
	If mHit
		PlaySound(soundCancel1)
		close=3
	EndIf
	bNF=0
	bLF=0
	bCF=1
Else
	bNF=0
	bLF=0
	bCF=0
EndIf
FlushMouse()

If KeyHit(1)
	close=3
EndIf

WaitTimer(gameTimer)
Flip
Until close
FlushKeys()
FlushMouse()
FreeImage(backTex)
FreeImage(mouseDot)
FreeImage(buttonNew)
FreeImage(buttonLoad)
FreeImage(buttonCancel)
Return close
End Function
;;;;;;;;;;;;;;;;;;;;
Function Tutorial()

tTimer = CreateTimer(30)

backTex = LoadImage("Levels\watertex.jpg")
howTo1 = LoadImage("MenuObjects\howto1.bmp")
howTo2 = LoadImage("MenuObjects\howto2.bmp")
howTo3 = LoadImage("MenuObjects\howto3.bmp")
howTo4 = LoadImage("MenuObjects\howto4.bmp")
howTo5 = LoadImage("MenuObjects\howto5.bmp")
MaskImage(howTo1, 255, 0, 255)
MaskImage(howTo2, 255, 0, 255)
MaskImage(howTo3, 255, 0, 255)
MaskImage(howTo4, 255, 0, 255)
MaskImage(howTo5, 255, 0, 255)

bgX = 0
bgY = 0

currentPage = 1

FlushKeys()
FlushMouse()

Repeat
Cls

bgX = bgX + 1
bgY = bgY - 1

If bgX > 255
	bgX = 0
EndIf
If bgY < 0
	bgY = 255
EndIf

If MouseHit(1)
	currentPage = currentPage + 1
ElseIf KeyHit(1)
	Exit
EndIf

If currentPage > 5
	Exit
EndIf

TileImage(backTex, bgX, bgY)
Select currentPage
	Case 1
		DrawImage(howTo1, 211, 88)
	Case 2
		DrawImage(howTo2, 211, 88)
	Case 3
		DrawImage(howTo3, 211, 88)
	Case 4
		DrawImage(howTo4, 211, 88)
	Case 5
		DrawImage(howTo5, 211, 88)
End Select
WaitTimer(tTimer)
Flip
Forever

FreeTimer(tTimer)
FreeImage(backTex)

End Function
;;;;;;;;;;;;;;;;;;;;
Function BonusPoints(remainingTime% = 0)

bonus = 0 ;remainingTime * 10

activateChest = 0

If remainingTime > 0
	ClearBullets()
	ClearPowerUps()
	ClearShells()
	For ball.balls=Each balls
		If ball<>Null
			gameBallCount=gameBallCount-1
			If ball\entity
				FreeEntity(ball\entity)
				ball\entity = 0
			EndIf
			Delete ball
		EndIf
	Next
	victoryMesh = LoadAnimMesh("Bricks\victory.b3d")
	Animate(victoryMesh, 3, .3)
	Repeat
	Cls
	gameWTimer=gameWTimer+1
	PositionTexture(gameWTex, gameWTimer*.001, 0)
	If activateChest = 1
		activateChest = 2
		chest = LoadAnimMesh("Bricks\chest.b3d")
		Animate(chest, 3, 1)
		EmitSound(soundCreak1, chest)
		PositionEntity(chest, 0, 20, 75)
		ScaleEntity(chest, .1, .1, .1)
		FlushKeys()
		FlushMouse()
	ElseIf activateChest = 2
		If (Not Animating(chest))
			activateChest = 3
			PositionEntity(brickEmitter, 0, 25, 65)
			SetEmitter(brickEmitter, treasureTemplate, True)
		EndIf
		If GetKey()
			Exit
		ElseIf GetMouse()
			Exit
		EndIf
		FlushKeys()
		FlushMouse()
	ElseIf activateChest = 3
		gameRoundTime = gameRoundTime - 10
		bonus = bonus + 100
		gamePlayerScore = gamePlayerScore + 100
		EmitSound(soundCoin1, chest)
		If gameRoundTime < 0
			gameRoundTime = 0
			;FreezeEmitter(chest)
			activateChest = 4
		EndIf
		If GetKey()
			Exit
		ElseIf GetMouse()
			Exit
		EndIf
		FlushKeys()
		FlushMouse()
	ElseIf activateChest = 4
		If GetKey()
			Exit
		ElseIf GetMouse()
			Exit
		EndIf
		FlushKeys()
		FlushMouse()
	EndIf
	UpdateWorld()
	l_update(2) ;RenderWorld()
	DrawHud()
	jf_text(gameFont, 600, 500, "Bonus Treasure  ", 1, 1)
	jf_text(gameFont, 600, 550, bonus, 1, 1)
	UpdatePirates(2)
	UpdateParrots()
	UpdateParticles()
	WaitTimer(gameTimer)
	Flip
	Forever
EndIf

If chest
	FreeEntity(chest)
	chest = 0
EndIf

If victoryMesh
	FreeEntity(victoryMesh)
	victoryMesh = 0
EndIf

FlushKeys()

Return bonus

End Function
;;;;;;;;;;;;;;;;;;;;
Function LoadSounds()
	If gameMusic1
		FreeSound(gameMusic1)
	EndIf
	If gameMusic2
		FreeSound(gameMusic2)
	EndIf
	If gameMusic3
		FreeSound(gameMusic3)
	EndIf
	If gameMusic4
		FreeSound(gameMusic4)
	EndIf
	If gameAmbient1
		FreeSound(gameAmbient1)
	EndIf
	If soundPHit
		FreeSound(soundPHit)
	EndIf
	If soundBHit
		FreeSound(soundBHit)
	EndIf
	If soundBreak
		FreeSound(soundBreak)
	EndIf
	If soundSand
		FreeSound(soundSand)
	EndIf
	If soundCannonShoot
		FreeSound(soundCannonShoot)
	EndIf
	If soundCannonS2
		FreeSound(soundCannonS2)
	EndIf
	If soundCannonHit
		FreeSound(soundCannonHit)
	EndIf
	If soundSquak1
		FreeSound(soundSquak1)
	EndIf
	If soundSquak2
		FreeSound(soundSquak2)
	EndIf
	If soundCoin1
		FreeSound(soundCoin1)
	EndIf
	If soundCreak1
		FreeSound(soundCreak1)
	EndIf
	If soundFare1
		FreeSound(soundFare1)
	EndIf
	If soundLose1
		FreeSound(soundLose1)
	EndIf
	If soundNew1
		FreeSound(soundNew1)
	EndIf
	If soundLoad1
		FreeSound(soundLoad1)
	EndIf
	If soundCancel1
		FreeSound(soundCancel1)
	EndIf
	If soundWater1
		FreeSound(soundWater1)
	EndIf
	gameMusic1=LoadSound("Music\Track1.wav")
	gameMusic2=LoadSound("Music\Track2.wav")
	gameMusic3=LoadSound("Music\Track3.wav")
	gameMusic4=LoadSound("Music\Track4.wav")
	LoopSound(gameMusic1)
	LoopSound(gameMusic2)
	LoopSound(gameMusic3)
	LoopSound(gameMusic4)
	SoundVolume(gameMusic1, .005 * musicVolume)
	SoundVolume(gameMusic2, .005 * musicVolume)
	SoundVolume(gameMusic3, .01 * musicVolume)
	SoundVolume(gameMusic4, .004 * musicVolume)
	;;;;;;;;;;;;;;;;;;;
	gameAmbient1 = LoadSound("SFX\waves.wav")
	LoopSound(gameAmbient1)
	SoundVolume(gameAmbient1, .005 * ambientVolume)
	;;;;;;;;;;;;;;;;;;;
	soundPHit = Load3DSound("SFX\pop.wav")
	soundBHit = Load3DSound("SFX\slap.wav")
	soundBreak = Load3DSound("SFX\break.wav")
	soundSand = Load3DSound("SFX\sandHit.wav")
	soundCannonShoot = Load3DSound("SFX\cannon1.wav")
	soundCannonS2 = Load3DSound("SFX\cannon2.wav")
	soundCannonHit = Load3DSound("SFX\cannonBrickHit.wav")
	soundSquak1 = Load3DSound("SFX\parrot1.wav")
	soundSquak2 = Load3DSound("SFX\parrot2.wav")
	soundCoin1 = Load3DSound("SFX\coin.wav")
	soundCreak1 = Load3DSound("SFX\dCreak.wav")
	soundFare1 = Load3DSound("SFX\fancyfare.wav")
	soundLose1 = Load3DSound("SFX\lose.wav")
	soundNew1 = Load3DSound("SFX\pirateslife.wav")
	soundLoad1 = Load3DSound("SFX\jollyrogaaa.wav")
	soundCancel1 = Load3DSound("SFX\rest.wav")
	soundWater1 = LoadSound("SFX\water1.wav")
	soundPick1 = LoadSound("SFX\slaop.wav")
	SoundVolume(soundPHit, .002 * sfxVolume)
	SoundVolume(soundBHit, .002 * sfxVolume)
	SoundVolume(soundBreak, .002 * sfxVolume)
	SoundVolume(soundSand, .003 * sfxVolume)
	SoundVolume(soundCannonShoot, .0015 * sfxVolume)
	SoundVolume(soundCannonS2, .01 * sfxVolume)
	SoundVolume(soundCannonHit, .001 * sfxVolume)
	SoundVolume(soundSquak1, .003 * sfxVolume * parrotSound)
	SoundVolume(soundSquak2, .005 * sfxVolume * parrotSound)
	SoundVolume(soundCoin1, .01 * sfxVolume)
	SoundVolume(soundCreak1, .01 * sfxVolume)
	SoundVolume(soundFare1, .01 * sfxVolume)
	SoundVolume(soundLose1, .01 * sfxVolume)
	SoundVolume(soundNew1, .01 * sfxVolume)
	SoundVolume(soundLoad1, .01 * sfxVolume)
	SoundVolume(soundCancel1, .01 * sfxVolume)
	SoundVolume(soundWater1, .01 * sfxVolume)
	SoundVolume(soundPick1, .01 * sfxVolume)
End Function
;;;;;;;;;;;;;;;;;;;;
Function OptionsMenu()

optionSprite = LoadSprite("MenuObjects\optionsmenu.bmp", 1+4)
EntityAlpha(optionSprite, 1);.5 .78
EntityOrder(optionSprite, -100)
EntityParent(optionSprite, gameCam)
PositionEntity(optionSprite, 0, 0, 5)
ScaleSprite(optionSprite, 4, 3);2,1.5)

mousesprite=LoadSprite("MenuObjects\mouse4.png",4)
EntityOrder(mousesprite, -103)
EntityParent(mousesprite, gameCam)
PositionEntity(mousesprite, 0, -3, 5)
ScaleSprite(mousesprite, 40, 40)
mousedot=CreateImage(1,1)
SetBuffer(ImageBuffer(mousedot))
Rect(0,0,1,1)
SetBuffer(BackBuffer())

FlushMouse()
FlushKeys()
FlushJoy()
;HideEntity(titlesprite)
;HideEntity(playsprite)
;HideEntity(optionssprite)
;HideEntity(creditssprite)
;HideEntity(exitsprite)
;ShowEntity(optionsmenusprite)
sliderSprite = LoadSprite("MenuObjects\slider.bmp", 1+4)
sliderSprite2 = LoadSprite("MenuObjects\slider.bmp", 1+4)
sliderSprite3 = LoadSprite("MenuObjects\slideer.bmp", 1+4)
sliderSprite4 = LoadSprite("MenuObjects\slideer.bmp", 1+4)
sliderSprite5 = LoadSprite("MenuObjects\slideer.bmp", 1+4)
oSave = LoadSprite("MenuObjects\oSave.bmp", 1+4)
oCancel = LoadSprite("MenuObjects\oCancel.bmp", 1+4)
EntityParent(sliderSprite, gameCam)
EntityParent(sliderSprite2, gameCam)
EntityParent(sliderSprite3, gameCam)
EntityParent(sliderSprite4, gameCam)
EntityParent(sliderSprite5, gameCam)
EntityParent(oSave, gameCam)
EntityParent(oCancel, gameCam)
EntityOrder(sliderSprite, -101)
EntityOrder(sliderSprite2, -101)
EntityOrder(sliderSprite3, -101)
EntityOrder(sliderSprite4, -101)
EntityOrder(sliderSprite5, -101)
EntityOrder(oSave, -101)
EntityOrder(oCancel, -101)
HideEntity(oSave)
HideEntity(oCancel)
ScaleSprite(sliderSprite, .18, .09)
ScaleSprite(sliderSprite2, .18, .09)
ScaleSprite(sliderSprite3, .18, .09)
ScaleSprite(sliderSprite4, .18, .09)
ScaleSprite(sliderSprite5, .18, .09)
ScaleSprite(oSave, .135, .043)
ScaleSprite(oCancel, .135, .043)
overSave = 0
overCancel = 0
If parrotSound = 1
	PositionEntity(sliderSprite, 1.382,  -.422, 5)
Else
	PositionEntity(sliderSprite, .9,  -.422, 5)
EndIf
If windowed <> 1
	PositionEntity(sliderSprite2, 1.382, -1.143, 5)
Else
	PositionEntity(sliderSprite2, .9, -1.143, 5)
EndIf
PositionEntity(oSave, -.324, -.4275, 1.01)
PositionEntity(oCancel, .3165, -.427, 1.01)
;3.59 * percent - .28
slX# = musicVolume * .035105 - .28
If slX < -.28
	slX = -.28
ElseIf slX > 3.2305
	slX = 3.2305
EndIf
PositionEntity(sliderSprite3, slX, 1.49, 5)
slX# = sfxVolume * .035105 - .28
If slX < -.28
	slX = -.28
ElseIf slX > 3.2305
	slX = 3.2305
EndIf
PositionEntity(sliderSprite4, slX, .87, 5)
slX# = ambientVolume * .035105 - .28
If slX < -.28
	slX = -.28
ElseIf slX > 3.2305
	slX = 3.2305
EndIf
PositionEntity(sliderSprite5, slX, .17, 5)
s3Min# = musicVolume * 3.61 - 50
s3Max# = musicVolume * 3.61 - 42
s4Min# = sfxVolume * 3.61 - 50
s4Max# = sfxVolume * 3.61 - 42
s5Min# = ambientVolume * 3.61 - 50
s5Max# = ambientVolume * 3.61 - 42
option_exit = 0
Repeat
	Cls
	;
	mx#=MouseX() - mw
	my#=-2 * MouseY() + (mh * 1);2
	PositionEntity(mousesprite, mx, my, mw);mx,my,mw);mx*.0095,my*.0095,5)
	;MoveEntity(water,.1,0,.1)
	;PositionEntity(skycube,EntityX(gameCam,True),EntityY(gameCam,True),EntityZ(gameCam,True))
	;TurnEntity(pivot,0,.1,0)
	;
	;parti.particlem=New particlem
	;parti\entity=CopyEntity(particleentity,mousesprite)
	;parti\time=0
	;parti\timer=18
	;parti\xspeed=0;Rand(-1,1)
	;parti\yspeed=5
	;parti\zspeed=1;Rand(-1,1)
	;parti\red=255
	;parti\green=255
	;parti\blue=0
	;parti\order=-110
	;parti\scale=20
	;EntityParent(parti\entity,0)
	;TurnEntity(parti\entity,0,Rand(0,360),0)
	;For parti.particlem=Each particlem
	;	EntityColor(parti\entity,parti\red,parti\green,parti\blue)
	;	;EntityOrder(parti\entity,parti\order)
	;	MoveEntity(parti\entity,0,0,1)
	;	ScaleSprite(parti\entity,parti\scale,parti\scale)
	;	TranslateEntity(parti\entity,0,parti\yspeed,0);parti\xspeed,parti\yspeed,parti\zspeed)
	;	TurnEntity(parti\entity,0,1,0)
	;	parti\red=parti\red-20
	;	;parti\green=parti\green+1
	;	;parti\blue=parti\blue+1
	;	;parti\order=parti\order+1
	;	parti\scale=parti\scale-1
	;	;parti\yspeed=parti\yspeed-2
	;	parti\time=parti\time+1
	;	If parti\time>parti\timer
	;		FreeEntity(parti\entity)
	;		Delete parti
	;	EndIf
	;Next
	
	;ph#=MilliSecs()/10.3       ;Frequency of waves (time)
	;wtrvrtcnt%=CountVertices(watersurf)-1
		
	;For k=0 To wtrvrtcnt Step 3            ;Loop through mesh and update vertices
	;	wtrvertx#=VertexX(watersurf,k)
	;	wtrvertz#=VertexZ(watersurf,k)
	;	wtrverty#=Sin(ph+wtrvertz*200)*wtrdepth ;Create wave
	;	VertexCoords watersurf,k,wtrvertx,wtrverty,wtrvertz ;Update
	;Next
	
	If mx > -230 And my < -198 And mx < -94 And my > -233
		If overSave <> 1
			overSave = 1
			ShowEntity(oSave)
		EndIf
	ElseIf mx > 90 And my < -198 And mx < 228 And my > -233
		If overCancel <> 1
			overCancel = 1
			ShowEntity(oCancel)
		EndIf
	Else
		If overSave <> 0
			overSave = 0
			HideEntity(oSave)
		EndIf
		If overCancel <> 0
			overCancel = 0
			HideEntity(oCancel)
		EndIf
	EndIf
	If Not MouseDown(1)
		sl3Down = False
		sl4Down = False
		sl5Down = False
	EndIf
	If sl3Down
		prevMusVol = musicVolume
		musicVolume = ((mx + 46.0) / 3.61)
		If musicVolume < 0
			musicVolume = 0
		ElseIf musicVolume > 100
			musicVolume = 100
		EndIf
		If prevMusVol <> musicVolume
			ResetSoundVolumes()
		EndIf
		slX# = mx / 102.83435 + 0.16732133;((mx + 46.0) / 3.61) * .035105 - .28
		If slX < -.28
			slX = -.28
		ElseIf slX > 3.2305
			slX = 3.2305
		Else
			s3Min = mx - 4
			s3Max = mx + 4
		EndIf
		PositionEntity(sliderSprite3, slX, 1.49, 5)
	ElseIf sl4Down
		sfxVolume = ((mx + 46.0) / 3.61)
		If sfxVolume < 0
			sfxVolume = 0
		ElseIf sfxVolume > 100
			sfxVolume = 100
		EndIf
		slX# = mx / 102.83435 + 0.16732133;((mx + 46.0) / 3.61) * .035105 - .28
		If slX < -.28
			slX = -.28
		ElseIf slX > 3.2305
			slX = 3.2305
		Else
			s4Min = mx - 4
			s4Max = mx + 4
		EndIf
		PositionEntity(sliderSprite4, slX, .87, 5)
	ElseIf sl5Down
		prevAmbVol = ambientVolume
		ambientVolume = ((mx + 46.0) / 3.61)
		If ambientVolume < 0
			ambientVolume = 0
		ElseIf ambientVolume > 100
			ambientVolume = 100
		EndIf
		If prevAmbVol <> ambientVolume
			ResetSoundVolumes()
		EndIf
		slX# = mx / 102.83435 + 0.16732133;((mx + 46.0) / 3.61) * .035105 - .28
		If slX < -.28
			slX = -.28
		ElseIf slX > 3.2305
			slX = 3.2305
		Else
			s5Min = mx - 4
			s5Max = mx + 4
		EndIf
		PositionEntity(sliderSprite5, slX, .17, 5)
	EndIf
	If MouseHit(1)
		If overSave
			SaveSettings()
			ResetSoundVolumes()
			option_exit = 1
		ElseIf overCancel
			option_exit = 2
		ElseIf my < 161 And my > 142 And mx > s3Min And mx < s3Max
			sl3Down = True
			sl4Down = False
			sl5Down = False
		ElseIf my < 98 And my > 79 And mx > s4Min And mx < s4Max
			sl3Down = False
			sl4Down = True
			sl5Down = False
		ElseIf my < 27 And my > 9 And mx > s5Min And mx < s5Max
			sl3Down = False
			sl4Down = False
			sl5Down = True
		ElseIf mx > 75 And my < -32 And mx < 158 And my > -51
			parrotSound = 1 - parrotSound
			If parrotSound = 1
				PositionEntity(sliderSprite, 1.382,  -.422, 5)
			Else
				PositionEntity(sliderSprite, .9,  -.422, 5)
			EndIf
		ElseIf mx > 75 And my < -109 And mx < 158 And my > -128
			windowed = 1 - windowed
			If windowed <> 1
				PositionEntity(sliderSprite2, 1.382, -1.143, 5)
			Else
				PositionEntity(sliderSprite2, .9, -1.143, 5)
			EndIf
		EndIf
	EndIf
	If KeyHit(pause_key)
		SaveSettings()
		ResetSoundVolumes()
		option_exit = 1
	ElseIf KeyHit(1)
		option_exit = 2
	EndIf
	;UpdateNormals(water)
	UpdateWorld()
	RenderWorld();l_update()
	DrawHud()
	DrawImage(mousedot,MouseX(),MouseY())
	;Text(0, 100, EntityX(mousesprite))
	;Text(0, 110, EntityY(mousesprite))
	;Text(0, 120, mx)
	;Text(0, 130, my)
	;Text(0, 140, EntityX(sliderSprite3))
	;Text(0, 150, s3Min)
	;Text(0, 160, s3Max)
	;Text(0, 170, "Slider3 Percentage: "+((EntityX(slidersprite3)+.28)/.035105))
	WaitTimer(timer)
	Flip
Until option_exit
If optionSprite
	FreeEntity(optionSprite)
	optionSprite = 0
EndIf
If mouseSprite
	FreeEntity(mouseSprite)
	mouseSprite = 0
EndIf
If sliderSprite
	FreeEntity(sliderSprite)
	sliderSprite = 0
EndIf
If sliderSprite2
	FreeEntity(sliderSprite2)
	sliderSprite2 = 0
EndIf
If sliderSprite3
	FreeEntity(sliderSprite3)
	sliderSprite3 = 0
EndIf
If sliderSprite4
	FreeEntity(sliderSprite4)
	sliderSprite4 = 0
EndIf
If sliderSprite5
	FreeEntity(sliderSprite5)
	sliderSprite5 = 0
EndIf
If oSave
	FreeEntity(oSave)
	oSave = 0
EndIf
If oCancel
	FreeEntity(oCancel)
	oCancel = 0
EndIf
;ShowEntity(titlesprite)
;ShowEntity(playsprite)
;ShowEntity(optionssprite)
;ShowEntity(creditssprite)
;ShowEntity(exitsprite)
;HideEntity(optionsmenusprite)
FlushKeys()
FlushJoy()
FlushMouse()
Return option_exit

End Function
;;;;;;;;;;;;;;;;;;;;
Function ResetSoundVolumes()
	SoundVolume(gameMusic1, .005 * musicVolume)
	SoundVolume(gameMusic2, .005 * musicVolume)
	SoundVolume(gameMusic3, .01 * musicVolume)
	SoundVolume(gameMusic4, .004 * musicVolume)
	If (levelSong = 1 Or levelSong = 2)
		ChannelVolume(gameChnlMusic, .005 * musicVolume)
	ElseIf levelSong = 3
		ChannelVolume(gameChnlMusic, .01 * musicVolume)
	Else
		ChannelVolume(gameChnlMusic, .004 * musicVolume)
	EndIf
	;
	SoundVolume(gameAmbient1, .005 * ambientVolume)
	ChannelVolume(gameChnlAmbient, .005 * ambientVolume)
	;
	SoundVolume(soundPHit, .002 * sfxVolume)
	SoundVolume(soundBHit, .002 * sfxVolume)
	SoundVolume(soundBreak, .002 * sfxVolume)
	SoundVolume(soundSand, .003 * sfxVolume)
	SoundVolume(soundCannonShoot, .0015 * sfxVolume)
	SoundVolume(soundCannonS2, .01 * sfxVolume)
	SoundVolume(soundCannonHit, .001 * sfxVolume)
	SoundVolume(soundSquak1, .003 * sfxVolume * parrotSound)
	SoundVolume(soundSquak2, .005 * sfxVolume * parrotSound)
	SoundVolume(soundCoin1, .01 * sfxVolume)
	SoundVolume(soundCreak1, .01 * sfxVolume)
	SoundVolume(soundFare1, .01 * sfxVolume)
	SoundVolume(soundLose1, .01 * sfxVolume)
	SoundVolume(soundNew1, .01 * sfxVolume)
	SoundVolume(soundLoad1, .01 * sfxVolume)
	SoundVolume(soundCancel, .01 * sfxVolume)
	SoundVolume(soundWater1, .01 * sfxVolume)
	SoundVolume(soundPick1, .01 * sfxVolume)
End Function
;;;;;;;;;;;;;;;;;;;;
Function HighScoreTable(rank% = 0)
	name1$ = ""
	name2$ = ""
	name3$ = ""
	name4$ = ""
	name5$ = ""
	name6$ = ""
	name6$ = ""
	name7$ = ""
	name8$ = ""
	name9$ = ""
	name10$ = ""
	score1$ = ""
	score2$ = ""
	score3$ = ""
	score4$ = ""
	score5$ = ""
	score6$ = ""
	score7$ = ""
	score8$ = ""
	score9$ = ""
	score10$ = ""
	file = OpenFile("Saves\Scores\scores.gfx")
	name1 = ReadString(file)
	name2 = ReadString(file)
	name3 = ReadString(file)
	name4 = ReadString(file)
	name5 = ReadString(file)
	name6 = ReadString(file)
	name7 = ReadString(file)
	name8 = ReadString(file)
	name9 = ReadString(file)
	name10 = ReadString(file)
	score1 = ReadString(file)
	score2 = ReadString(file)
	score3 = ReadString(file)
	score4 = ReadString(file)
	score5 = ReadString(file)
	score6 = ReadString(file)
	score7 = ReadString(file)
	score8 = ReadString(file)
	score9 = ReadString(file)
	score10 = ReadString(file)
	CloseFile(file)
	
	jf_create_text(gameFont,127,63,gamePName,1,1)
	
	hsTimer = CreateTimer(30)
	
	hsBackground = LoadImage("MenuObjects\watertex.jpg")
	hsBgX = 0
	hsBgY = 255
	
	hsTable = LoadImage("MenuObjects\highScores.bmp")
	MaskImage(hsTable, 255, 0, 255)
	MidHandle(hsTable)
	n1Y = 305
	
	hsCancel = LoadAnimImage("MenuObjects\cancelA.bmp", 250, 128, 0, 2)
	HandleImage(hsCancel, 124, 0)
	
	leafA = LoadAnimImage("MenuObjects\leaf64a.bmp", 64, 32, 0, 2)
	HandleImage(leafA, 31, 15)
	leafFrame = 0
	leafTime = 0
	
	hsPointer = LoadImage("MenuObjects\pointer2.bmp")
	
	FlushKeys()
	FlushMouse()
	
	Repeat
	Cls
	
	hsBgX = hsBgX + 1
	hsBgY = hsBgY - 1
	
	TileImage(hsBackground, hsBgX, hsBgY)
	DrawImage(hsTable, 511, 383)
	jf_text(gameFont, 511, n1Y, name1+"----------------"+score1,1,1)
	jf_text(gameFont, 511, n1Y+30, name2+"----------------"+score2,1,1)
	jf_text(gameFont, 511, n1Y+60, name3+"----------------"+score3,1,1)
	jf_text(gameFont, 511, n1Y+90, name4+"----------------"+score4,1,1)
	jf_text(gameFont, 511, n1Y+120, name5+"----------------"+score5,1,1)
	jf_text(gameFont, 511, n1Y+150, name6+"----------------"+score6,1,1)
	jf_text(gameFont, 511, n1Y+180, name7+"----------------"+score7,1,1)
	jf_text(gameFont, 511, n1Y+210, name8+"----------------"+score8,1,1)
	jf_text(gameFont, 511, n1Y+240, name9+"----------------"+score9,1,1)
	jf_text(gameFont, 511, n1Y+270, name10+"----------------"+score10,1,1)
	
	If rank <> 0
		leafTime = leaftime + 1
		If leafTime > 5
			leafTime = 0
			leafFrame = 1 - leafFrame
		EndIf
		DrawImage(leafA, 265, n1Y+(30*(rank-1)), leafFrame)
	EndIf
	
	hsMX = MouseX()
	hsMY = MouseY()
	
	If RectsOverlap(hsMX, hsMY, 1, 1, 388, 656, 247, 90)
		DrawImage(hsCancel, 511, 630, 1)
		If MouseHit(1)
			Exit
		EndIf
	Else
		DrawImage(hsCancel, 511, 630, 0)
	EndIf
	
	DrawImage(hsPointer, hsMX, hsMY)
	
	WaitTimer(hsTimer)
	Flip
	Until KeyHit(1) Or KeyHit(28)
	FreeImage(hsCancel)
	FreeImage(hsPointer)
	FreeTimer(hsTimer)
	FreeImage(hsBackground)
	FreeImage(hsTable)
End Function
;;;;;;;;;;;;;;;;;;;;
Function CompareHighScores(name$, score%)
	rv = 0
	name1$ = ""
	name2$ = ""
	name3$ = ""
	name4$ = ""
	name5$ = ""
	name6$ = ""
	name6$ = ""
	name7$ = ""
	name8$ = ""
	name9$ = ""
	name10$ = ""
	score1$ = ""
	score2$ = ""
	score3$ = ""
	score4$ = ""
	score5$ = ""
	score6$ = ""
	score7$ = ""
	score8$ = ""
	score9$ = ""
	score10$ = ""
	file = OpenFile("Saves\Scores\scores.gfx")
	name1 = ReadString(file)
	name2 = ReadString(file)
	name3 = ReadString(file)
	name4 = ReadString(file)
	name5 = ReadString(file)
	name6 = ReadString(file)
	name7 = ReadString(file)
	name8 = ReadString(file)
	name9 = ReadString(file)
	name10 = ReadString(file)
	score1 = ReadString(file)
	score2 = ReadString(file)
	score3 = ReadString(file)
	score4 = ReadString(file)
	score5 = ReadString(file)
	score6 = ReadString(file)
	score7 = ReadString(file)
	score8 = ReadString(file)
	score9 = ReadString(file)
	score10 = ReadString(file)
	;
	If score > 1000000000
		score = 1000000000
	EndIf
	scoreString$ = score
	scoreLength = Len(scoreString)
	sLength = 3
	sPosition = 3
	Repeat
	If sPosition >= scoreLength
		Exit
	EndIf
	
	
	scoreString = Left(scoreString, (scoreLength - sPosition)) + "," + Right(scoreString, sPosition)
	
	scoreLength = Len(scoreString)
	sLength = sLength + 3
	sPosition = sPosition + 4
	
	Until sLength >= scoreLength
	;
	CloseFile(file)
	If score > Int(Replace(score1, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = score6
		score6 = score5
		score5 = score4
		score4 = score3
		score3 = score2
		score2 = score1
		score1 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name6
		name6 = name5
		name5 = name4
		name4 = name3
		name3 = name2
		name2 = name1
		name1 = name
		rv = 1
	ElseIf score > Int(Replace(score2, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = score6
		score6 = score5
		score5 = score4
		score4 = score3
		score3 = score2
		score2 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name6
		name6 = name5
		name5 = name4
		name4 = name3
		name3 = name2
		name2 = name
		rv = 2
	ElseIf score > Int(Replace(score3, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = score6
		score6 = score5
		score5 = score4
		score4 = score3
		score3 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name6
		name6 = name5
		name5 = name4
		name4 = name3
		name3 = name
		rv = 3
	ElseIf score > Int(Replace(score4, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = score6
		score6 = score5
		score5 = score4
		score4 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name6
		name6 = name5
		name5 = name4
		name4 = name
		rv = 4
	ElseIf score > Int(Replace(score5, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = score6
		score6 = score5
		score5 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name6
		name6 = name5
		name5 = name
		rv = 5
	ElseIf score > Int(Replace(score6, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = score6
		score6 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name6
		name6 = name
		rv = 6
	ElseIf score > Int(Replace(score7, ",", ""))
		score10 = score9
		score9 = score8
		score8 = score7
		score7 = scoreString
		name10 = name9
		name9 = name8
		name8 = name7
		name7 = name
		rv = 7
	ElseIf score > Int(Replace(score8, ",", ""))
		score10 = score9
		score9 = score8
		score8 = scoreString
		name10 = name9
		name9 = name8
		name8 = name
		rv = 8
	ElseIf score > Int(Replace(score9, ",", ""))
		score10 = score9
		score9 = scoreString
		name10 = name9
		name9 = name
		rv = 9
	ElseIf score > Int(Replace(score10, ",", ""))
		score10 = scoreString
		name10 = name
		rv = 10
	EndIf
	file = WriteFile("Saves\Scores\scores.gfx")
	WriteString(file, name1)
	WriteString(file, name2)
	WriteString(file, name3)
	WriteString(file, name4)
	WriteString(file, name5)
	WriteString(file, name6)
	WriteString(file, name7)
	WriteString(file, name8)
	WriteString(file, name9)
	WriteString(file, name10)
	WriteString(file, score1)
	WriteString(file, score2)
	WriteString(file, score3)
	WriteString(file, score4)
	WriteString(file, score5)
	WriteString(file, score6)
	WriteString(file, score7)
	WriteString(file, score8)
	WriteString(file, score9)
	WriteString(file, score10)
	CloseFile(file)
	Return(rv)
End Function
;;;;;;;;;;;;;;;;;;;;
Function Fail()
	failTimer = CreateTimer(30)
	
	If gameCam
		CameraProjMode(gameCam, 0)
	EndIf
	tempCam = cam
	cam = CreateCamera()
	PositionEntity(cam, 0, 10, -20)
	
	PlaySound(soundLose1)
	
	failBackground = LoadSprite("MenuObjects\fail.dds", 1, cam)
	EntityBlend(failBackground, 1)
	EntityOrder(failBackground, -198)
	ScaleSprite(failBackground, 4, 3)
	PositionEntity(failBackground, 0, 0, 4)
	
	failMesh = LoadAnimMesh("Bricks\fail.b3d")
	Animate(failMesh, 3, .2)
	EntityOrder(FindChild(failMesh,"Text01"), -200)
	EntityOrder(FindChild(failMesh,"Text02"), -199)
	PositionEntity(failMesh, 0, 2, 0)
	ScaleEntity(failMesh, .2, .2, .2)
	
	tX = 370
	
	FlushKeys()
	FlushMouse()
	
	Repeat
	Cls
	
	If GetKey()
		Exit
	ElseIf GetMouse()
		Exit
	EndIf
	
	;MoveEntity(failBackground, .1, .1, 0)
	
	WaitTimer(failTimer)
	UpdateWorld()
	RenderWorld()
	jf_text(gameFont, tX, 550, "Press",0,1)
	jf_text(gameFont, tX+75, 550, "Any",0,1)
	jf_text(gameFont, tX+140, 550, "Key",0,1)
	jf_text(gameFont, tX+200, 550, "to",0,1)
	jf_text(gameFont, tX+240, 550, "Continue.",0,1)
	Flip
	Until KeyHit(1)
	
	FreeEntity(failMesh)
	FreeEntity(cam)
	cam = tempcam
	If gameCam
		CameraProjMode(gameCam, 1)
	EndIf
	FreeTimer(failTimer)
	FlushMouse()
	FlushKeys()
End Function
;;;;;;;;;;;;;;;;;;;;
Function DancingCredits()
	If gameCam
		CameraProjMode(gameCam, 0)
	EndIf
	tempCam = cam
	cam = CreateCamera()
	discoMusic = LoadSound("Music\PirateDance.wav")
	LoopSound(discoMusic)
	discoChnl = PlaySound(discoMusic)
	FlushMouse()
	FlushKeys()
	FlushJoy()
	discoFloor = LoadAnimMesh("MenuObjects\DiscoCredits\disco.b3d")
	ScaleEntity(discoFloor, .1, .1, .1)
	MoveEntity(cam, 0, 8, -12)
	PointEntity(cam, discoFloor)
	TurnEntity(discoFloor, 0, 45, 0)
	EntityOrder(FindChild(discoFloor,"GeoSphere01"), -9)
	EntityOrder(FindChild(discoFloor,"Sphere01"), -10)
	Animate(discoFloor, 1, .1)
	pirate = LoadAnimMesh("MenuObjects\DiscoCredits\pirate.b3d")
	ExtractAnimSeq(pirate, 221, 400)
	ExtractAnimSeq(FindChild(pirate, "Box01"), 221, 400)
	TurnEntity(pirate, 0, 180, 0)
	ScaleEntity(pirate, .03, .03, .03)
	MoveEntity(pirate, 0, 0, 0)
	Animate(pirate, 1, .4, 1)
	Animate(FindChild(pirate, "Box01"), 1, .4, 1)
	;;;;;;;;;;;;;;;;;;;
	;;;;;;;;;;;;;;;;;;;
	;       Blur      ;
	;;;;;;;;;;;;;;;;;;;
	l_surface2 = 0
	l_texture2 = 0
	If Not l_surface2
		l_surface2=CreateSprite(cam)
		width=GraphicsWidth()
		height=GraphicsHeight();Global
		
		SpriteViewMode l_surface2,2
		PositionEntity l_surface2,0,0,1.001
		EntityOrder l_surface2,-90
		ScaleSprite l_surface2, 1, 0.75
		EntityAlpha l_surface2, .86
	EndIf
	If Not l_texture2
		l_texture2 = CreateTexture(width,Height,1+256)
		ScaleTexture l_texture2, (Float TextureWidth(l_texture2)/Float width),(Float TextureHeight(l_texture2)/Float Height)
		
		EntityTexture l_surface2, l_texture2
		TextureBlend l_texture2, 2
	EndIf
	;;;;;;;;;;;;;;;;;;;
	;;;;;;;;;;;;;;;;;;;
	creditsName1=CopyEntity(cN1,cam);LoadSprite("MenuObjects\Programmer.bmp",1+4)
	EntityOrder(creditsName1,-101)
	;EntityParent(creditsName1,cam)
	;PositionEntity(creditsName1,0,-8,5)
	ScaleSprite(creditsName1,4,1.33333)
	creditsName2=CopyEntity(cN2,cam);LoadSprite("MenuObjects\ShaneDaniels.bmp",1+4)
	EntityOrder(creditsName2,-101)
	;EntityParent(creditsName2,cam)
	;PositionEntity(creditsName2,0,-11,5)
	ScaleSprite(creditsName2,2,.6666666666)
	creditsName3=CopyEntity(cN3,cam);LoadSprite("MenuObjects\Artist.bmp",1+4)
	EntityOrder(creditsName3,-101)
	;EntityParent(creditsName3,cam)
	;PositionEntity(creditsName3,0,-8,5)
	ScaleSprite(creditsName3,4,1.33333)
	creditsName4=CopyEntity(cN4,cam);LoadSprite("MenuObjects\JesseMitchell.bmp",1+4)
	EntityOrder(creditsName4,-101)
	;EntityParent(creditsName4,cam)
	;PositionEntity(creditsName4,0,-11,5)
	ScaleSprite(creditsName4,2,.6666666666)
	creditsName5 = CopyEntity(cN5, cam)
	EntityOrder(creditsName5, -101)
	ScaleSprite(creditsName5, 4, 1.33333)
	creditsName6 = CopyEntity(cN6, cam)
	EntityOrder(creditsName6, -101)
	ScaleSprite(creditsName6, 4, 1.33333)
	;;;;;;;;;;;;;;;;;;;
	PositionEntity(creditsName1,0,0,0)
	PositionEntity(creditsName2,0,0,0)
	PositionEntity(creditsName3,0,0,0)
	PositionEntity(creditsName4,0,0,0)
	PositionEntity(creditsName5, 0, 0, 0)
	PositionEntity(creditsName6, 0, 0, 0)
	MoveEntity(creditsName1,0,-5,5)
	MoveEntity(creditsName2,0,-10,5)
	MoveEntity(creditsName3,0,-16,5)
	MoveEntity(creditsName4,0,-21,5)
	MoveEntity(creditsName5, 0, -27, 5)
	MoveEntity(creditsName6, 0, -32, 5)
	creditsSpeed#=.01
	creditsTime=0
	Repeat
	
	creditsTime=creditsTime+1
	
	MoveEntity(creditsName1,0,creditsSpeed,0)
	MoveEntity(creditsName2,0,creditsSpeed,0)
	MoveEntity(creditsName3,0,creditsSpeed,0)
	MoveEntity(creditsName4,0,creditsSpeed,0)
	MoveEntity(creditsName5, 0, creditsSpeed, 0)
	MoveEntity(creditsName6, 0, creditsSpeed, 0)
	
	If GetKey()
		Exit
	ElseIf GetMouse()
		Exit
	EndIf
		
	UpdateWorld()
	l_update(1, l_texture2);RenderWorld()
	Flip
	Until creditsTime>3700
	FlushMouse()
	FlushKeys()
	FlushJoy()
	FreeEntity(creditsName1)
	FreeEntity(creditsName2)
	FreeEntity(creditsName3)
	FreeEntity(creditsName4)
	FreeEntity(creditsName5)
	FreeEntity(creditsName6)
	FreeEntity(pirate)
	FreeEntity(discoFloor)
	StopChannel(discoChnl)
	FreeSound(discoMusic)
	FreeEntity(cam)
	cam = tempCam
	If gameCam
		CameraProjMode(gameCam, 1)
	EndIf
End Function
;;;;;;;;;;;;;;;;;;;;