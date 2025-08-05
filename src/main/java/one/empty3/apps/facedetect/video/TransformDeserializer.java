package one.empty3.apps.facedetect.video;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Un deserializer personnalisé pour gérer la hiérarchie de classes de Transform.
 * Il lit la propriété "type" de l'objet JSON pour déterminer quelle sous-classe
 * concrète de Transform doit être instanciée.
 */
public class TransformDeserializer implements JsonDeserializer<Transform> {

    @Override
    public Transform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        Transform transform;

        // Créer l'instance appropriée selon le type
        switch (type.toLowerCase()) {
            case "attachimage":
                transform = new TransformAttachImage();
                break;
            case "detachimage":
                transform = new TransformDetachImage();
                break;
            case "translate":
                transform = new TransformTranslate();
                break;
            case "rotate":
                transform = new TransformRotate();
                break;
            case "setvisibility":
                transform = new TransformSetVisibility();
                break;
            case "morph":
                transform = new TransformMorph();
                break;
            case "scale":
                transform = new TransformScale();
                break;
            default:
                throw new JsonParseException("Type de transformation inconnu : " + type);
        }

        // Désérialiser les propriétés communes
        if (jsonObject.has("type")) {
            transform.setType(jsonObject.get("type").getAsString());
        }

        if (jsonObject.has("frames")) {
            transform.setFrames(jsonObject.get("frames").getAsInt());
        }

        if (jsonObject.has("visible")) {
            transform.setVisible(jsonObject.get("visible").getAsBoolean());
        }

        // Gérer la propriété target (structure complexe)
        if (jsonObject.has("target")) {
            JsonObject targetObject = jsonObject.getAsJsonObject("target");

            // Extraire le targetType
            if (targetObject.has("type")) {
                String targetTypeStr = targetObject.get("type").getAsString();
                Transform.TargetType targetType = targetTypeStr.equalsIgnoreCase("group")
                        ? Transform.TargetType.Group
                        : Transform.TargetType.All;
                transform.setTargetType(targetType);
            }

            // Extraire le targetId depuis groupId
            if (targetObject.has("groupId")) {
                transform.setTargetId(targetObject.get("groupId").getAsString());
            }
        }

        // Désérialiser les propriétés spécifiques selon le type
        switch (type.toLowerCase()) {
            case "attachimage":
            case "attach_image":
                TransformAttachImage attachImage = (TransformAttachImage) transform;
                if (jsonObject.has("imageUrl")) {
                    attachImage.setImageUrl(jsonObject.get("imageUrl").getAsString());
                }
                break;

            case "translate":
                TransformTranslate translate = (TransformTranslate) transform;
                if (jsonObject.has("dx")) {
                    translate.setDx(jsonObject.get("dx").getAsDouble());
                }
                if (jsonObject.has("dy")) {
                    translate.setDy(jsonObject.get("dy").getAsDouble());
                }
                break;

            case "rotate":
                TransformRotate rotate = (TransformRotate) transform;
                if (jsonObject.has("angle")) {
                    rotate.setAngle(jsonObject.get("angle").getAsDouble());
                }
                if (jsonObject.has("cx")) {
                    rotate.setCx(jsonObject.get("cx").getAsDouble());
                }
                if (jsonObject.has("cy")) {
                    rotate.setCy(jsonObject.get("cy").getAsDouble());
                }
                break;

            case "scale":
                TransformScale scale = (TransformScale) transform;
                if (jsonObject.has("scaleX")) {
                    scale.setScaleX(jsonObject.get("scaleX").getAsDouble());
                }
                if (jsonObject.has("scaleY")) {
                    scale.setScaleY(jsonObject.get("scaleY").getAsDouble());
                }
                if (jsonObject.has("cx")) {
                    scale.setCx(jsonObject.get("cx").getAsDouble());
                }
                if (jsonObject.has("cy")) {
                    scale.setCy(jsonObject.get("cy").getAsDouble());
                }
                break;

            case "setvisibility":
                TransformSetVisibility visibility = (TransformSetVisibility) transform;
                if (jsonObject.has("visible")) {
                    visibility.setVisible(jsonObject.get("visible").getAsBoolean());
                }
                break;

            case "morph":
                TransformMorph morph = (TransformMorph) transform;
                if (jsonObject.has("sourceGroupId")) {
                    morph.setSourceGroupId(jsonObject.get("sourceGroupId").getAsString());
                }
                if (jsonObject.has("targetGroupId")) {
                    morph.setTargetGroupId(jsonObject.get("targetGroupId").getAsString());
                }
                break;
        }

        return transform;
    }

}