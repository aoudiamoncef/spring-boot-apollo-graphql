package com.lahzouz.graphql.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.apollographql.apollo3.api.Adapter;
import com.apollographql.apollo3.api.CustomScalarAdapters;
import com.apollographql.apollo3.api.json.JsonReader;
import com.apollographql.apollo3.api.json.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;




@Component
public class DateGraphQLAdapter implements Adapter<Date> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date fromJson(@NotNull final JsonReader jsonReader, @NotNull final CustomScalarAdapters customScalarAdapters) throws IOException {
        try {
            return DATE_FORMAT.parse(jsonReader.nextString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toJson(@NotNull final JsonWriter jsonWriter, @NotNull final CustomScalarAdapters customScalarAdapters, final Date date) throws IOException {
        jsonWriter.value(DATE_FORMAT.format(date));
    }
}
