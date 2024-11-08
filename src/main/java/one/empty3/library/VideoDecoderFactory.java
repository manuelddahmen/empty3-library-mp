/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.library;


import one.empty3.libs.Image;
import java.io.File;
public class VideoDecoderFactory {
    /***
     * Creates and start video decoder (frame by frame, no sound)
     * @param f Movie
     * @param m Texture extends TextureMov
     * @return VideoDecoder thread instance
     */
     public static VideoDecoder createInstance(File f, TextureMov m)
      {
          VideoDecoder decode = 
     new DecodeJcodec(f, m);
          return decode;
      }


}

