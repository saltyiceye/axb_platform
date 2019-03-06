package com.yt.axb

import groovy.transform.CompileStatic

@CompileStatic
enum Status {
    TEST(-1),
    IDLE(0),
    ASSIGNED(1),

    final int id
    private Status(int id) { this.id = id }
}
