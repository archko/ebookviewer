# Dragon Pdf

Dragon Pdf is a highly customizable Dragon Pdf for Android.

Supports the following formats:
* PDF
* DjVu
* EPUB
* XPS (OpenXPS)
* CBZ (Comic Books, no support for rar compressed CBR)
* FictionBook (fb2)

Collaboration with electronic publication sites and access to online ebook catalogs is allowed by the supported OPDS protocol.

FAQ, information about supported MIME types, and available Intents can be found in the [Wiki](https://github.com/dschuermann/document-viewer/wiki).

## [Changelog](https://raw.githubusercontent.com/SufficientlySecure/document-viewer/HEAD/document-viewer/src/main/assets/about/en/changelog.wiki)

## Development

Dragon Pdf is a fork of the last GPL version of EBookDroid (http://code.google.com/p/ebookdroid/).

We need your support to fix outstanding bugs, join development by forking the project!

## Building

**NOTE: NDK r14b fails to compile DV - use r15 or r13b. (See [#245](https://github.com/SufficientlySecure/document-viewer/issues/245))**

### Build with Gradle

1. Have Android SDK "tools", "platform-tools", and "build-tools" directories in your PATH (http://developer.android.com/sdk/index.html)
2 Open the Android SDK Manager (shell command: ``android``).
Expand the Tools directory and select "Android SDK Build-tools" newest version.
Expand the Extras directory and install "Android Support Repository"
Select everything for the newest SDK
3. Export ANDROID_HOME pointing to your Android SDK
4. Execute ``./gradlew build``

### NDK Debugging

1. ``cd document-viewer; ndk-build -j8 NDK_DEBUG=1``
2. From Android Studio: Run -> Debug... to build and install the APK and launch it on the device.
3. ``cp src/main/AndroidManifest.xml . # Hack required for ndk-gdb to find everything``
4. ``ndk-gdb``

### Development with Android Studio

I am using the newest [Android Studio](http://developer.android.com/sdk/installing/studio.html) for development. Development with Eclipse is currently not possible because I am using the new [project structure](http://developer.android.com/sdk/installing/studio-tips.html).

1. Clone the project from github
2. From Android Studio: File -> Import Project -> Select the cloned top folder
3. Import project from external model -> choose Gradle

# Licenses
Dragon Pdf is licensed under the GPLv3+.
The file LICENSE includes the full license text.

## Details
Dragon Pdf is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Dragon Pdf is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Dragon Pdf.  If not, see <http://www.gnu.org/licenses/>.

## Java Libraries
* JCIFS
  http://jcifs.samba.org/
  LGPL v2.1

* Color Picker by Daniel Nilsson
  http://code.google.com/p/color-picker-view/
  Apache License v2

## C Libraries

* MuPDF - a lightweight PDF, EPUB, CBZ and XPS viewer
  http://www.mupdf.com/
  AGPLv3+

* djvu - a lightweight DJVU viewer based on DjVuLibre
  http://djvu.sourceforge.net/
  GPLv2

## Images

* application_icon.svg
  http://rrze-icon-set.berlios.de/
  Creative Commons Attribution Share-Alike licence 3.0
