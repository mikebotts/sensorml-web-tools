
package com.sensia.swetools.editors.sensorml.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.sensia.gwt.relaxNG.RNGInstanceWriter;
import com.sensia.gwt.relaxNG.RNGParser;
import com.sensia.gwt.relaxNG.RNGParserCallback;
import com.sensia.gwt.relaxNG.XMLSerializer;
import com.sensia.relaxNG.RNGGrammar;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SensorMLEditor implements EntryPoint
{
    ResizeLayoutPanel rngPanel;
    PopupPanel popup = new PopupPanel();
    RNGGrammar loadedGrammar;
    TextArea area;
    
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        RNGParser.clearCache();
        
        VerticalPanel panel = new VerticalPanel();
        
        // buttons
        Button b1 = new Button("Be Relax", new ClickHandler() {
            public void onClick(ClickEvent event) {
                area = null;
                openRNGSchema();
            }
        });
        b1.setSize("100", "30");
        panel.add(b1);
        
        Button b2 = new Button("See it", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (loadedGrammar == null)
                    return;
                RNGInstanceWriter writer = new RNGInstanceWriter();
                Document dom = writer.writeInstance(loadedGrammar);
                
                TabLayoutPanel tabs = (TabLayoutPanel)rngPanel.getWidget();
                                
                if (area == null)
                {
                    area = new TextArea();
                    area.setWidth("100%");
                    area.setHeight("100%");
                    tabs.add(area, "SensorML Output");
                }
                
                //area.setText(dom.toString());
                area.setText(XMLSerializer.serialize(dom));
                tabs.selectTab(tabs.getWidgetCount()-1);
            }
        });
        b2.setSize("100", "30");
        panel.add(b2);
        
        // resize panel to hold tabs
        // tabs don work correctly if we don have it
        rngPanel = new ResizeLayoutPanel();
        rngPanel.setPixelSize(1024, 768);        
        panel.add(rngPanel);
        
        // test tab panel
        //TabLayoutPanel tabs = new TabLayoutPanel(2.5, Unit.EM);
        //tabs.setAnimationDuration(1000);
        //tabs.add(new HTML("this content"), "this");
        //tabs.add(new HTML("that content"), "that");
        //tabs.add(new HTML("the other content"), "the other");
        //rngPanel.setWidget(tabs);
        
        RootPanel root = RootPanel.get("editor-area");
        root.add(panel);
    }
    
   
    protected void openRNGSchema()
    {
        /*String url = "sml_editor/rng1.0/profiles/CSM/frame-sensor-model.rng";
        
        popup.setWidget(new HTML("Loading RelaxNG schema"));
        popup.show();
        
        RNGServiceAsync client = GWT.create(RNGService.class);
        client.loadGrammar(url, new AsyncCallback<RNGGrammar>() {
            @Override
            public void onFailure(Throwable caught)
            {
                popup.setWidget(new HTML("Error while loading RelaxNG schema"));             
            }

            @Override
            public void onSuccess(RNGGrammar result)
            {
                System.out.println("Rendering schema");
                RNGRendererSML renderer = new RNGRendererSML();
                renderer.visit(result);
                rngPanel.clear();
                rngPanel.add(renderer.getWidgets().get(0));
                popup.hide();                
            }
        });*/
        
        String url = "rng1.0/profiles/CSM/frame-sensor-model.rng";
        
        popup.setWidget(new HTML("Loading RelaxNG schema"));
        popup.show();
        
        RNGParser parser = new RNGParser();
        parser.parse(url, new RNGParserCallback() {
            @Override
            public void onParseDone(RNGGrammar grammar)
            {
                System.out.println("Rendering schema");
                RNGRendererSML renderer = new RNGRendererSML();
                renderer.visit(grammar);
                rngPanel.clear();
                rngPanel.add(renderer.getWidgets().get(0));
                popup.hide();
                loadedGrammar = grammar;
            }
        });
    }
}
