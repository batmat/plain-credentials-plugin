/*
 * The MIT License
 *
 * Copyright 2013 jglick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.plaincredentials.impl;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import hudson.model.BuildListener;
import hudson.model.Item;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.Nonnull;

public abstract class Binding<C extends StandardCredentials> extends AbstractDescribableImpl<Binding<C>> {

    private final String variable;
    private final String credentialsId;

    protected Binding(String variable, String credentialsId) {
        this.variable = variable;
        this.credentialsId = credentialsId;
    }

    protected abstract Class<C> type();

    public String getVariable() {
        return variable;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public interface Environment {

        String value();

        void unbind() throws IOException, InterruptedException;

    }

    public abstract Environment bind(@Nonnull AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException;

    protected @Nonnull C getCredentials(@Nonnull Item owner) throws IOException {
        for (C c : CredentialsProvider.lookupCredentials(type(), owner, null, Collections.<DomainRequirement>emptyList())) {
            if (c.getId().equals(credentialsId)) {
                return c;
            }
        }
        throw new FileNotFoundException(credentialsId);
    }

}
