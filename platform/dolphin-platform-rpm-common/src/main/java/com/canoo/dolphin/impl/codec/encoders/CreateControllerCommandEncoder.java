/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.dolphin.impl.commands.CreateControllerCommand;
import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import static com.canoo.dolphin.impl.codec.CommandConstants.*;

public class CreateControllerCommandEncoder implements CommandEncoder<CreateControllerCommand> {

    @Override
    public JsonObject encode(final CreateControllerCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(PARENT_CONTROLLER_ID, command.getParentControllerId());
        jsonCommand.addProperty(CONTROLLER_NAME, command.getControllerName());
        jsonCommand.addProperty(ID, CREATE_CONTROLLER_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public CreateControllerCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            final CreateControllerCommand command = new CreateControllerCommand();
            if(jsonObject.has(PARENT_CONTROLLER_ID) && !jsonObject.get(PARENT_CONTROLLER_ID).isJsonNull()) {
                command.setParentControllerId(jsonObject.getAsJsonPrimitive(PARENT_CONTROLLER_ID).getAsString());
            }
            command.setControllerName(jsonObject.getAsJsonPrimitive(CONTROLLER_NAME).getAsString());
            return command;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
