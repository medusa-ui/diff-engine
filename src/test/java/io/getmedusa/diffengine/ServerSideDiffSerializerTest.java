package io.getmedusa.diffengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.model.HTMLLayer;
import org.joox.JOOX;
import org.junit.jupiter.api.Test;

class ServerSideDiffSerializerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSerialization() throws JsonProcessingException {
        var diff = ServerSideDiff.buildNewBeforeDiff(new HTMLLayer(JOOX.$("<p/>")), new HTMLLayer(JOOX.$("<p/>")));
        final String json = mapper.writeValueAsString(diff);
        //System.out.println(json);
    }

}
