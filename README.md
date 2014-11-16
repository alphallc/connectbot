sshIt
=========

Copyright (c) 2014 AlphaLLC Android Dev Dept <android@alpha-llc.org>
Copyright (c) 2014 ConnectBot Community (see commitlog)

## Description

sshIt is open-source telnet and secure shell (SSH) client, based on ConnectBot and it's successors.

!!! Warning: work on moving to another crypto backends is in progress. Client may become unusable in any moment.

## Features and enhancements

 - based on (VX and original) ConnectBot 1.7.1
 - background file transfer (SCP protocol)
 - screen capture (save a PNG screenshot of the console)
 - character picker dialog (on-screen and hardware SYM keys)
 - single line input (on-screen key)
 - tap-and-hold to toggle full screen mode or change font size
 - tap-and hold on on-screen buttons with various manus
 - ssh-agent from Roberto Tyley
 - X11 forwarding support
 - SOCKS5 Proxy support
 - Color Theme managment
 - Public key registration (ssh-copy-id analog)

### New key mappings:

 - ALT + Up Arrow maps to Page Up
 - ALT + Down Arrow maps to Page Down
 - ALT + Left Arrow maps to Home
 - ALT + Right Arrow maps to End
 - ALT + Backspace maps to Insert
 - Search key maps to URL scan

## Device Customization

Our base project (VX ConnectBot), before it's freeze, was aimed to provide customizations for Android devices with a physical keyboard, and we keeping that enchancements in place.

Full list of supported devices and individual key mappings can be viewed [here](http://connectbot.vx.sk/customkeymap.html)

## License

sshIt is licensed under the Apache License, Version 2.0

## Download

[sshIt on Android Market](https://market.android.com/details?id=org.alphallc.sshit) (alpha, so you need to join https://plus.google.com/communities/110104323370703606070 and sign up on testing: https://play.google.com/apps/testing/org.alphallc.sshit)

## Credits

This software initially based on [VX ConnectBot](https://github.com/vx/connectbot)

VX ConnectBot Copyright (c) 2012 Martin Matuška <martin at matuska dot vx dot sk>

Which, in his part, is based on "original" [ConnectBot](http://code.google.com/p/connectbot/)

ConnectBot Copyright (c) 2007-2011 [Kenny Root](http://the-b.org), [Jeffrey Sharkey](http://jsharkey.org)

Based in part on the [Trilead SSH2 client](http://www.trilead.com), provided under a BSD-style license.  Copyright (c) 2007 Trilead AG.

Also based on [JTA Telnet/SSH client](http://www.javassh.org), provided under the GPLv2 license. Copyright (c) Matthias L. Jugel, Marcus Meiner 1996-2005.

Also based in part on the [JSOCKS](http://jsocks.sourceforge.net) library, provided under the GNU LGPL license. (c) 

Also based in part on [JZlib](http://www.jcraft.com) provided under a BSD-style license. Copyright (c) JCraft, Inc., 2000-2004

This software includes modifications from [Irssiconnectbot](https://github.com/irssiconnectbot/irssiconnectbot) developed by Iiro Uusitalo and Ville Kerminen.

This software includes the [ssh-agent](https://github.com/rtyley) service developed by Roberto Tyley.

SCP file transfer support is badsed on [modifications](https://github.com/staktrace/connectbot/commits/filetransfer) by Kartikaya Gupta.

Internal file selection dialog is based on [Android File Dialog](http://code.google.com/p/android-file-dialog/) by Alexander Ponomarev, provided under a BSD-style license.
