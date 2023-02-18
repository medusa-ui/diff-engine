package io.getmedusa.diffengine.engine;

import io.getmedusa.diffengine.model.HTMLLayer;
import org.joox.JOOX;
import org.joox.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTMLLayerBuildupEngineLogic {

    private HTMLLayerBuildupEngineLogic() {}

    static Match initialParse(String html) {
        //clear scripts, clear comments, etc
        final Document document = Jsoup.parse(html);
        removeComments(document);
        document.outputSettings().prettyPrint(false);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return JOOX.$(document.body().outerHtml().replace("&", "&amp;"));
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

    public static Map<Integer, List<HTMLLayer>> interpretLayer(String html) {
        return interpretLayer(initialParse(html));
    }

    private static Map<Integer, List<HTMLLayer>> interpretLayer(Match match) {
        Map<Integer, List<HTMLLayer>> layerMap = new HashMap<>();
        layerMap.put(1, List.of(new HTMLLayer(match)));
        recursive(match, layerMap, 2);
        return layerMap;
    }

    private static void recursive(Match match, Map<Integer, List<HTMLLayer>> map, int layer) {
        List<HTMLLayer> layers = map.getOrDefault(layer, new LinkedList<>());
        for (var child : match.children()) {
            var $child = JOOX.$(child);
            layers.add(new HTMLLayer($child));
            if($child.children().isNotEmpty()) {
                recursive($child, map, layer + 1);
            }
        }
        map.put(layer, layers);
    }
}
