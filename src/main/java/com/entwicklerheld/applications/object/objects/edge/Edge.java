package com.entwicklerheld.applications.object.objects.edge;

import com.entwicklerheld.applications.object.core.OBDDObject;

public record Edge(OBDDObject<?, ?> source, OBDDObject<?, ?> target, boolean branche) {

}
