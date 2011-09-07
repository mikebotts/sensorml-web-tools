/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are Copyright (C) 2011 Sensia Software LLC.
 All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package com.sensia.swetools.editors.sensorml.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p><b>Title:</b>
 * LibraryMetadata
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Metadata associated to resources in library
 * </p>
 *
 * <p>Copyright (c) 2010</p>
 * @author Alexandre Robin
 * @date Sep 2, 2011
 */
public class LibraryMetadata implements Serializable
{
    private static final long serialVersionUID = -6267007917576242692L;
    
    protected String id;
    protected String label;
    protected String description;
    protected String creator;
    protected Date dateCreated;
    protected Date dateModifieded;    
    protected int version;
    protected List<String> tags = new ArrayList<String>(3);


    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getLabel()
    {
        return label;
    }


    public void setLabel(String label)
    {
        this.label = label;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getCreator()
    {
        return creator;
    }


    public void setCreator(String creator)
    {
        this.creator = creator;
    }


    public Date getDateCreated()
    {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }


    public Date getDateModifieded()
    {
        return dateModifieded;
    }


    public void setDateModifieded(Date dateModifieded)
    {
        this.dateModifieded = dateModifieded;
    }


    public int getVersion()
    {
        return version;
    }


    public void setVersion(int version)
    {
        this.version = version;
    }


    public List<String> getTags()
    {
        return tags;
    }
}
