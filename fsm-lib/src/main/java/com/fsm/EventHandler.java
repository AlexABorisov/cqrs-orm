/*!
*
* Copyright VimpelCom, Ltd., 2013.  All rights reserved.
*
* This software or any other copies thereof may not
* be provided or otherwise made available to any other person.  No title to
* and ownership of the software is hereby transferred.
*
* \file application/src/main/java/ru/beeline/netphone/fms/InvokerFSM.java
* \brief Invoker FMS interface
*
*/

package com.fsm;

public interface EventHandler<T> {
    int invoke(T evt);
}
