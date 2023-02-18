package io.getmedusa.diffengine.engine;

import org.joox.Match;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HTMLLayerBuildupEngineLogicTest {

    @Test
    void testBasicHTMLParse() {
        Match match = HTMLLayerBuildupEngineLogic.initialParse("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Example</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <p>This is an example of a simple HTML page with one paragraph.</p>\n" +
                "    </body>\n" +
                "</html>");
        Assertions.assertNotNull(match);
    }

    @Test
    void testAbilityToDealWithComments() {
        Match match = HTMLLayerBuildupEngineLogic.initialParse("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <!-- head definitions go here -->\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <!-- the content goes here -->\n" +
                "    </body>\n" +
                "</html>");
        Assertions.assertNotNull(match);
    }

    @Test
    void testAbilityToDealWithNBSP() {
        Match match = HTMLLayerBuildupEngineLogic.initialParse("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Example</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <p>This is an example of a simple HTML page with a paragraph.</p>&nbsp;<p>And another paragraph.</p>\n" +
                "    </body>\n" +
                "</html>");
        Assertions.assertNotNull(match);
    }

}
