package org.techconnect.model;

import java.util.List;

/**
 * Created by Phani on 10/28/2016.
 */

public interface Commentable {

    List<Comment> getComments();

    String getId();

    String getParentType();

}
