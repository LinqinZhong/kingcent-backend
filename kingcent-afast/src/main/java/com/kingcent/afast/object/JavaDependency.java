package com.kingcent.afast.object;

import lombok.AllArgsConstructor;

/**
 * @author rainkyzhong
 * @date 2024/10/19 18:13
 */
public class JavaDependency {
    public String groupId;
    public String artifactId;
    public String version;
    public String scope;

    public JavaDependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public JavaDependency(String groupId, String artifactId, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
    }
}
