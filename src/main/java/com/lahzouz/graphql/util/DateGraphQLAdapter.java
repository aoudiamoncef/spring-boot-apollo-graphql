package com.lahzouz.graphql.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;

import lombok.SneakyThrows;


@Component
public class DateGraphQLAdapter implements CustomTypeAdapter<Date> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@SneakyThrows
	@Override
	public Date decode(@NotNull final CustomTypeValue<?> customTypeValue) {
		return DATE_FORMAT.parse(customTypeValue.value.toString());
	}

	@NotNull
	@Override
	public CustomTypeValue<?> encode(final Date date) {
		return new CustomTypeValue.GraphQLString(DATE_FORMAT.format(date));
	}
}
