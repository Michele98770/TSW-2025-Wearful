package control;

import model.prodotto.ProdottoBean;
import java.util.Map;

public class JsonUtil {

    public static String convertProductsToJson(Map<String, Map<String, ProdottoBean>> productsByColorAndSize) {
        StringBuilder jsonProducts = new StringBuilder("{");
        boolean firstColor = true;
        for (Map.Entry<String, Map<String, ProdottoBean>> colorEntry : productsByColorAndSize.entrySet()) {
            if (!firstColor) jsonProducts.append(",");
            jsonProducts.append("\"").append(colorEntry.getKey()).append("\":{");
            boolean firstSize = true;
            for (Map.Entry<String, ProdottoBean> sizeEntry : colorEntry.getValue().entrySet()) {
                if (!firstSize) jsonProducts.append(",");
                ProdottoBean pb = sizeEntry.getValue();
                jsonProducts.append("\"").append(sizeEntry.getKey()).append("\":{");
                jsonProducts.append("\"id\":").append(pb.getId()).append(",");
                jsonProducts.append("\"nome\":\"").append(escapeJsonString(pb.getNome())).append("\",");
                jsonProducts.append("\"descrizione\":\"").append(escapeJsonString(pb.getDescrizione())).append("\",");
                jsonProducts.append("\"prezzo\":").append(pb.getPrezzoFinale()).append(",");
                jsonProducts.append("\"disponibilita\":").append(pb.getDisponibilita()).append(",");
                jsonProducts.append("\"imgPath\":\"").append(escapeJsonString(pb.getImgPath())).append("\"");
                jsonProducts.append("}");
                firstSize = false;
            }
            jsonProducts.append("}");
            firstColor = false;
        }
        jsonProducts.append("}");
        return jsonProducts.toString();
    }

    // Metodo helper per effettuare l'escape delle stringhe per JSON (es. apici, backslash)
    private static String escapeJsonString(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}