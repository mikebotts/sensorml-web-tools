/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are Copyright (C) 2011 Sensia Software LLC.
 All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package com.sensia.swetools.editors.sensorml.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sensia.relaxNG.RNGGrammar;
import com.sensia.swetools.editors.sensorml.shared.LibraryMetadata;


/**
 * Client side stub for RelaxNG service
 */
@RemoteServiceRelativePath("rng_service")
public interface RNGService extends RemoteService
{
    public RNGGrammar loadGrammar(String url);
    
    public String saveGrammar(LibraryMetadata metadata, RNGGrammar grammar);
}
