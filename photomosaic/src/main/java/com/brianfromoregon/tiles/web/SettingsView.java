package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Tuple;
import com.brianfromoregon.tiles.persist.Repository;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.brianfromoregon.tiles.Tuple.tuple;

@ViewWith("settings.ftl")
public class SettingsView {
    @FormParam("imagemagick") @Getter @Setter private String imagemagick;
}
