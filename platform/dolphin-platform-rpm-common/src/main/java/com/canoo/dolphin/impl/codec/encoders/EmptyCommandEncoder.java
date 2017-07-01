package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;
import org.opendolphin.core.comm.EmptyCommand;

import static com.canoo.dolphin.impl.codec.CommandConstants.EMPTY_COMMAND_ID;
import static com.canoo.dolphin.impl.codec.CommandConstants.ID;

@Deprecated
public class EmptyCommandEncoder extends AbstractCommandEncoder<EmptyCommand> {
    @Override
    public JsonObject encode(EmptyCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, EMPTY_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public EmptyCommand decode(JsonObject jsonObject) {
        return new EmptyCommand();
    }
}