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

package one.empty3.feature20220726.motion;

import one.empty3.feature20220726.PixM;

import javaAnd.awt.image.Image;

import java.util.ArrayList;

/*
 * Motion
 * resize part (+/-/show/hide), move part, rotate part
 */
public abstract class Motion /*extends ProcessFile */ {
    public static final int BUFFER_MAX_FRAMES = 26;
    public ArrayList<Image> frames = new ArrayList<>();


    public boolean addFrame(Image bufferedImage) {
        if (bufferedImage != null) {
            this.frames.add(bufferedImage);
        }
        return frames.size() > BUFFER_MAX_FRAMES;
    }

    public one.empty3.libs.Image processFrame() {
        PixM frame1 = null;
        PixM frame2 = null;
        if (frames.size() == 0 || frames.get(0) == null)
            return null;
        if (frames.size() >= 2 && frames.size() < BUFFER_MAX_FRAMES) {

            frame1 = new PixM(frames.get(0).bufferedImage);
            frame2 = new PixM(frames.get(1).bufferedImage);
            frames.remove(0);
        } else if (frames.size() >= BUFFER_MAX_FRAMES) {
            frame1 = new PixM(frames.get(0).bufferedImage);
            frame2 = new PixM(frames.get(1).bufferedImage);
            frames.remove(0);
        } else {
            return null;
        }

        return process(frame1, frame2);
    }

    public abstract one.empty3.libs.Image process(PixM frame1, PixM frame2);

}
