/*
 * This file is part of APLP: KIGB, licensed under the MIT License
 *
 * Copyright (c) 2022 TheSilkMiner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.thesilkminer.mc.austin.mappings

import groovy.transform.CompileStatic

@CompileStatic
class LoadedMappings {
    // moj class name (with dots) to map of moj -> srg names
    final Map<String, Map<String, List<String>>> methods
    final Map<String, Map<String, String>> fields
    final Set<String> mappable

    LoadedMappings(Map<String, Map<String, List<String>>> methods, Map<String, Map<String, String>> fields) {
        this.methods = methods
        this.fields = fields

        List<String> outQueue = new ArrayList<>()
        methods.forEach((k,v) -> {
            List<String> queue = new ArrayList<>()
            v.forEach((off,srg) -> {
                List<String> queueSrg = new ArrayList<>()
                srg.forEach(a->{
                    if (off==a) queueSrg.add(a)
                })
                queueSrg.forEach(a->srg.remove(a))
                if (srg.isEmpty()) queue.add(off)
            })
            queue.forEach (it->v.remove(it))

            if (v.isEmpty()) {
                outQueue.add(k)
            }
        })
        outQueue.forEach {methods.remove(it)}

        outQueue.clear()
        fields.forEach((k,v) -> {
            List<String> queue = new ArrayList<>()
            v.forEach((off,srg) -> {
                if (off==srg) queue.add(off)
                return
            })
            queue.forEach(it->v.remove(it))

            if (v.isEmpty()) {
                outQueue.add(k)
            }
        })
        outQueue.forEach {fields.remove(it)}

        this.mappable = new HashSet<>(methods.keySet())
        this.mappable.addAll(fields.keySet())
    }
}