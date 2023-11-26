// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

public class UnableToSaveException extends Throwable {
    public UnableToSaveException(Exception e) {
        super(e);
    }
}
