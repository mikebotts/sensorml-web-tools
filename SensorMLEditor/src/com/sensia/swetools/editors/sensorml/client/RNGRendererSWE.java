/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are Copyright (C) 2011 Sensia Software LLC.
 All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package com.sensia.swetools.editors.sensorml.client;

import java.util.Map;
import com.google.gwt.user.client.ui.*;
import com.sensia.relaxNG.*;


/**
 * <p><b>Title:</b>
 * RNGRenderer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Renders content of an RNG grammar describing SWE Common data components
 * using GWT widgets
 * </p>
 *
 * <p>Copyright (c) 2011</p>
 * @author Alexandre Robin
 * @date Aug 27, 2011
 */
public class RNGRendererSWE extends RNGRenderer implements RNGTagVisitor
{
    protected final static String STYLE_HREF = "xlink";
    protected final static String STYLE_OBJ = "swe-obj";
    protected final static String SWE_NS = "http://www.opengis.net/swe/1.0.1";
    
    protected Map<String, String> softTypedProperties;
    
    
    public RNGRendererSWE()
    {        
    }


    @Override
    public void visit(RNGElement elt)
    {
        String eltName = elt.getName();
               
        if (eltName.startsWith("Boolean") ||
            eltName.startsWith("Quantity") ||
            eltName.startsWith("Count") ||
            eltName.startsWith("Category") ||
            eltName.startsWith("Time") ||
            eltName.equals("Text") ||
            eltName.equals("DataRecord") ||
            eltName.equals("Vector") ||
            eltName.equals("DataArray") ||
            eltName.equals("Matrix") ||
            eltName.equals("DataChoice") ||
            eltName.equals("DataStream"))
        {
            renderDataComponent(elt);
        }
        
        else if (eltName.equals("field") ||
                 eltName.equals("coordinate") ||
                 eltName.equals("elementType") ||
                 eltName.equals("item") ||
                 eltName.equals("quality") ||
                 eltName.equals("encoding"))
        {
            //renderDataComponentProperty(elt);
            renderPropertyPanel(elt);
        }
        
        else if (eltName.equals("identifier") ||
                 eltName.equals("label") ||
                 eltName.equals("description") ||
                 eltName.equals("uom") ||
                 eltName.equals("value"))
        {
            renderLabeledField(elt, toNiceLabel(elt.getName())); 
        }
        
        else
        {
            super.visit(elt);
        }
    }
    
    
    @Override
    public void visit(RNGAttribute att)
    {
        String attName = att.getName();
        
        if (attName.equals("name"))
        {
            visitChildren(att.getChildren());
            widgets.peek().get(0).setTitle("Enter name");
        }
        
        else if (attName.equals("code"))
        {
            visitChildren(att.getChildren());
        }
        
        else
        {
            renderLabeledField(att, toNiceLabel(att.getName())); 
        }
    }
    
    
    protected void renderPropertyPanel(RNGElement elt)
    {
        String title = toNiceLabel(elt.getName());
        
        DecoratorPanel decorator = new DecoratorPanel();
        DisclosurePanel hidePanel = new DisclosurePanel();
        hidePanel.setAnimationEnabled(true);
        hidePanel.setOpen(true);
        decorator.add(hidePanel);
        
        // header section
        HorizontalPanel header = new HorizontalPanel();
        header.addStyleName("swe-section-header");
        hidePanel.setHeader(header);
        
        // title in header
        Label label = new Label(title);
        label.addStyleName("swe-section-title");
        header.add(label);
        
        // content section
        //VerticalPanel contentPanel = new VerticalPanel();
        FlowPanel contentPanel = new FlowPanel();
        hidePanel.setContent(contentPanel);
        
        newWidgetList();
        visitChildren(elt.getChildren());
        for (Widget w: widgets.pop())
        {
            if (w.getTitle().equals("Enter name"))
                header.add(w);
            else
                contentPanel.add(w);
        }
        
        widgets.peek().add(decorator);
    }
    
    
    protected void renderDataComponentProperty(RNGElement elt)
    {
        DisclosurePanel hidePanel = new DisclosurePanel();
        hidePanel.setAnimationEnabled(true);
        
        VerticalPanel panel = new VerticalPanel();
        hidePanel.setContent(panel);
        
        // name attribute
        for (RNGTag child: elt.getChildren())
        {
            newWidgetList();
            child.accept(this);
            
            if (widgets.peek().size() > 0 && widgets.peek().get(0).getTitle().equals("Enter name"))
            {
                // assign name attribute widget to header
                Widget w = widgets.pop().get(0);
                hidePanel.setHeader(w);
            }
            else
            {
                for (Widget w: widgets.pop())
                    panel.add(w);
            }
        }
        
        widgets.peek().add(hidePanel);
    }
    
    
    protected void renderDataComponent(RNGElement elt)
    {
        Label label = new Label("Type: " + toNiceLabel(elt.getName()));
        label.addStyleName("swe-object-type");
        widgets.peek().add(label);
        visitChildren(elt.getChildren());
    }
    
    
    protected void renderLabeledField(RNGTagList tagList, String label)
    {
        HorizontalPanel panel = new HorizontalPanel();
        panel.addStyleName("swe-simple-field");
        
        String tooltip = tagList.getAnnotation();
        if (tooltip != null)
            panel.setTitle(tooltip);
        
        if (label != null)
            panel.add(new Label(label + ":"));
        
        newWidgetList();
        this.visitChildren(tagList.getChildren());
        addWidgetsToPanel(panel);
    }
    
    
    @Override
    protected String findLabel(RNGTag tag)
    {
        if (tag instanceof RNGElement)
        {
            String eltName = ((RNGElement)tag).getName();            
            if (eltName.equals("field") ||
                eltName.equals("item"))
            {
                RNGAttribute nameAtt = ((RNGElement)tag).getChildAttribute("name");
                RNGValue val = nameAtt.getChildValue();
                if (val != null)
                    return val.getText() + " " + toNiceLabel(eltName);
            }
        }
            
        return super.findLabel(tag);
    }
}
