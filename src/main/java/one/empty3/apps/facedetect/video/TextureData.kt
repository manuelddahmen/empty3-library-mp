package one.empty3.apps.facedetect.video

import one.empty3.apps.facedetect.jvm.ConvexHull
import one.empty3.apps.facedetect.jvm.Dimension
import one.empty3.apps.facedetect.jvm.DistanceAB
import one.empty3.apps.facedetect.jvm.DistanceBezier2
import one.empty3.library.Point3D
import one.empty3.library.objloader.E3Model
import one.empty3.libs.Image

class TextureData {
    lateinit var distanceAB: DistanceAB
    var isBezier : Boolean = true
    var algorithm: Int = 0
    var convexHull3: ConvexHull? = null
    var imageFileRight: Image? = null
    var pointsInImage: Map<String, Point3D>? = null
    var convexHull1: ConvexHull? = null
    var pointsInModel: Map<String, Point3D>? = null
    var convexHull2: ConvexHull? = null
    var hdTextures: Boolean = false
    var points3: Map<String, Point3D>? = null
    var image: Image? = null
    lateinit var dimPictureBox: Dimension
    var opt1: Boolean = false
    var optimizeGrid: Boolean = false
    var distanceABClass: Class<out DistanceAB?>? = null
    var model: E3Model? = null

}
val defaultTextureDefault: TextureData = TextureData()
