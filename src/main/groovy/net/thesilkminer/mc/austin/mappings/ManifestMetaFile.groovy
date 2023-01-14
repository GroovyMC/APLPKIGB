/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.mappings

import com.google.gson.annotations.Expose
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@POJO
@CompileStatic
class ManifestMetaFile {
    @Expose
    List<VersionMeta> versions

    class VersionMeta {
        @Expose
        String id
        @Expose
        String url
        @Expose
        String sha1
    }
}
