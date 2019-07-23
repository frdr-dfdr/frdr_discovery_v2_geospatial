package Crosswalking.XML;

import Dataverse.DataverseJavaObject;
import org.w3c.dom.Element;

public abstract class SubElement {
    protected XMLDocument doc;
    protected DataverseJavaObject djo;
    protected Element root;

    public SubElement(DataverseJavaObject djo, XMLDocument doc, Element root) {
        this.doc = doc;
        this.djo = djo;
        this.root = root;
    }
    public abstract Element getFields();
    //Creates the label and value elements of a parent and returns the parent
    protected Element setValChild(Element parent, String title, String val, String valType) {
        Element subTitle = doc.createGMDElement(title);
        subTitle.appendChild(doc.addGCOVal(val, valType));
        parent.appendChild(subTitle);

        return parent;
    }

    protected Element levelRoleCode(String name){
        Element levelRoleCode = doc.create_Element("role");
        levelRoleCode.appendChild(doc.addRoleCode(name));
        return levelRoleCode;
    }
}

