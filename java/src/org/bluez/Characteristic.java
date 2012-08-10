
package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.Variant;

import java.util.Map;

/**
 * Interface for connecting to org.bluez.Characteristic
 * 
 * comes from doc/attribute-api.txt on ics-mr0-release
 * 
 * DBus path:
 * - remote: [prefix]/{hci0}/{device0}/{service0}/{characteristic0,...}
 * - local: [prefix]/{hci0}/{service0}/{characteristic0,...}
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 */
public interface Characteristic extends DBusInterface {
    
    @SuppressWarnings("rawtypes")
    /**
     * Returns all properties for the characteristic.
     * 
     * String UUID [readonly]
     *      UUID128 of this characteristic.
     * String Name [readonly]
     *      Optional field containing a friendly name for the
     *      Characteristic UUID.
     * String Description [readonly]
     *      Textual optional characteristic descriptor describing
     *      the Characteristic Value.
     * Struct Format [readonly]
     *      Optional Characteristic descriptor which defines the
     *      format of the Characteristic Value. For numeric
     *      values, the actual value can be value * 10^Exponent.
     *      NameSpace and Description are defined on the Assigned
     *      Number Specification.
     *      uint8  | Format: format of the value
     *      uint8  | Exponent: Field to determine how the value is
     *             | further formatted.
     *      uint16 | Unit: unit of the characteristic
     *      uint8  | NameSpace: Name space of description.
     *      uint16 | Description: Description of the characteristic defined
     *             | in a high layer profile.
     *
     * List<Byte> Value [readwrite]
     *      Raw value of the Characteristic Value attribute
     * String Representation (of binary value) [readonly]
     *      Friendly representation of the Characteristic Value
     *      based on the format attribute.
     * 
     */
    public Map<String, Variant> GetProperties();
    
    /**
     * Changes the value of the specified property. Only
     * read-write properties can be changed. On success
     * this will emit a PropertyChanged signal.
     */
    @SuppressWarnings("rawtypes")
    void SetProperty(String name, Variant value) throws
            Error.InvalidArguments;
    
    /**
     * Class that matches the struct Format from GetProperties
     */
    public final class Format extends Struct  
    {  
       @Position(0)  
       public final Byte Format;  
       @Position(1)  
       public final Byte Exponent;
       @Position(2)  
       public final UInt16 Unit;
       @Position(3)
       public final Byte NameSpace;
       @Position(4)
       public final UInt16 Description;
       public Format(Byte f, Byte e, UInt16 u, Byte ns, UInt16 d)  
       {  
           this.Format = f;
           this.Exponent = e;
           this.Unit = u;
           this.NameSpace = ns;
           this.Description = d;
       }
    }
}
