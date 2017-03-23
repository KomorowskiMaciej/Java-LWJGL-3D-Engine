package Engine.modules.fonts.fontRendering;

import Engine.modules.fonts.fontMeshCreator.FontType;
import Engine.modules.fonts.fontMeshCreator.GUIText;
import Engine.modules.fonts.fontMeshCreator.TextMeshData;
import Engine.modules.resourceMenegment.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {

    private static Loader loader;
    private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
    private static FontRenderer renderer;

    public static void init() {
        renderer = new FontRenderer();
    }

    public static void render() {
        renderer.render(texts);
    }

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.getInstance().loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<GUIText>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            texts.remove(texts.get(text.getFont()));
        }
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

}
