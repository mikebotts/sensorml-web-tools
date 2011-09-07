/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are Copyright (C) 2011 Sensia Software LLC.
 All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package com.sensia.swetools.editors.sensorml.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sensia.relaxNG.RNGGrammar;
import com.sensia.swetools.editors.sensorml.client.RNGService;
import com.sensia.swetools.editors.sensorml.shared.LibraryMetadata;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RNGServiceImpl extends RemoteServiceServlet implements RNGService
{

    @Override
    public RNGGrammar loadGrammar(String url)
    {
        return null;
    }


    @Override
    public String saveGrammar(LibraryMetadata metadata, RNGGrammar grammar)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
