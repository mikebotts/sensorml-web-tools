/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are Copyright (C) 2011 Sensia Software LLC.
 All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package com.sensia.swetools.editors.sensorml.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sensia.relaxNG.RNGGrammar;
import com.sensia.swetools.editors.sensorml.shared.LibraryMetadata;


/**
 * The async counterpart of <code>RNGService</code>.
 */
public interface RNGServiceAsync
{
    void loadGrammar(String url, AsyncCallback<RNGGrammar> callback);

    void saveGrammar(LibraryMetadata metadata, RNGGrammar grammar, AsyncCallback<String> callback);
}
