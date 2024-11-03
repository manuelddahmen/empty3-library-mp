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

package testfeatures;

import one.empty3.feature.jviolajones.Detector;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import java.util.Objects;

public class DetectorTestOnMyImages {
    @Test
    public void testDetector() throws IOException {
        for (File img : Objects.requireNonNull(new File("resources/img/faces/").listFiles())) {
            if (img.getName().endsWith("jpg") || img.getName().endsWith("png")) {
                try {
                    InputStream haarXml = Detector.class.getResourceAsStream("/haarcascade_frontalface_alt2.xml");

                    Detector detector = new Detector(haarXml);
                    List<Rectangle> res = null;
                    res = detector.getFaces(img.getAbsolutePath(), 1, 1.25f, 0.1f, 1, true);

                    if (res.size() == 0) {
                        System.out.printf("No face\n");
                    } else {
                        for (Rectangle re : res) {
                            System.out.printf("Face %d\n%d\n%d\n%d\n",
                                    re.x, re.y, re.width, re.height);

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
