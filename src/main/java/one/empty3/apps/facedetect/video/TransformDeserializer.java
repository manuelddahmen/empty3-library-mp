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
        JsonElement typeElement = jsonObject.get("type");

        if (typeElement == null || typeElement.isJsonNull()) {
            throw new JsonParseException("L'objet Transform n'a pas de propriété 'type'");
        }

        String type = typeElement.getAsString();

        // Fait correspondre la valeur du type à la classe Java concrète
        Class<? extends Transform> transformClass;
        switch (type.toLowerCase()) {
            case "attach_image":
                transformClass = TransformAttachImage.class;
                break;
            case "detach_image":
                transformClass = TransformDetachImage.class;
                break;
            case "translate":
                transformClass = TransformTranslate.class;
                break;
            case "rotate":
                transformClass = TransformRotate.class;
                break;
            case "set_visibility":
                transformClass = TransformSetVisibility.class;
                break;
            case "morph":
                transformClass = TransformMorph.class;
                break;
            case "scale":
                transformClass = TransformScale.class;
                break;
            default:
                throw new JsonParseException("Type de Transform inconnu : " + type);
        }

        // Délègue la désérialisation à Gson, mais avec la classe concrète correcte
        return context.deserialize(jsonObject, transformClass);
    }
}