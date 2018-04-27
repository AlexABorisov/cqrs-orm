package com.cloud.database.api;

import java.util.Collection;
import java.util.List;

/**
 * Created by albo1013 on 17.11.2015.
 */
public interface DataAccessBulk<T,ID> extends DataAccessSingle<Collection<T>,List<ID>> {
}
