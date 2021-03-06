package ma.nawar.pharmarket.service.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import ma.nawar.pharmarket.domain.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * Created by HORRI on 05/04/2018.
 */
public class OrderDeserializer extends JsonDeserializer {

    /**
     * Method that can be called to ask implementation to deserialize
     * JSON content into the value type this serializer handles.
     * Returned instance is to be constructed by method itself.
     * <p>
     * Pre-condition for this method is that the parser points to the
     * first event that is part of value to deserializer (and which
     * is never JSON 'null' literal, more on this below): for simple
     * types it may be the only value; and for structured types the
     * Object start marker or a FIELD_NAME.
     * </p>
     * <p>
     * The two possible input conditions for structured types result
     * from polymorphism via fields. In the ordinary case, Jackson
     * calls this method when it has encountered an OBJECT_START,
     * and the method implementation must advance to the next token to
     * see the first field name. If the application configures
     * polymorphism via a field, then the object looks like the following.
     * <pre>
     *      {
     *          "@class": "class name",
     *          ...
     *      }
     *  </pre>
     * Jackson consumes the two tokens (the <tt>@class</tt> field name
     * and its value) in order to learn the class and select the deserializer.
     * Thus, the stream is pointing to the FIELD_NAME for the first field
     * after the @class. Thus, if you want your method to work correctly
     * both with and without polymorphism, you must begin your method with:
     * <pre>
     *       if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
     *         jp.nextToken();
     *       }
     *  </pre>
     * This results in the stream pointing to the field name, so that
     * the two conditions align.
     * <p>
     * Post-condition is that the parser will point to the last
     * event that is part of deserialized value (or in case deserialization
     * fails, event that was not recognized or usable, which may be
     * the same event as the one it pointed to upon call).
     * <p>
     * Note that this method is never called for JSON null literal,
     * and thus deserializers need (and should) not check for it.
     *
     * @param p    Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *             this deserialization activity.
     * @return Deserialized value
     */
    @Override
    public Ordre deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        Ordre order = objectMapper.readValue(p, Ordre.class);
        return order;
    }

    private Set<Shipping> getShippings(Object shippings) {
        List<Map<String, Object>> ListShipping = (ArrayList<Map<String, Object>>) shippings;
        Set<Shipping> shppingSet = new HashSet<Shipping>();
        if (ListShipping != null) {
            ListShipping.stream().forEach(shipping -> {
                Shipping sh = new Shipping();
                Long id = ((Integer) shipping.get("id")).longValue();
                sh.setId(id);
                shppingSet.add(sh);
            });
        }
        return shppingSet;

    }

    private Set<Pack> getPacks(Object packs) {
        List<Map<String, Object>> ListPacks = (ArrayList<Map<String, Object>>) packs;
        Set<Pack> listPack = new HashSet<Pack>();
        ListPacks.stream().forEach(packMap -> {
            Pack pack = new Pack();
            if (packMap.get("id") != null && !"0".equals(packMap.get("id"))) {
                pack.setId(Long.valueOf(packMap.get("id").toString()));
            }
            if (packMap.get("name") != null) {
                pack.setName(packMap.get("name").toString());
                pack.setRules(this.getRules(packMap.get("rules")));
                pack.setPackProducts(this.getPackProduct(packMap.get("packProducts")));
                listPack.add(pack);
            }
        });

        return listPack;
    }

    private Set<PackProduct> getPackProduct(Object packProducts) {
        List<Map<String, Object>> ListPackProducts = (ArrayList<Map<String, Object>>) packProducts;
        Set<PackProduct> packProductsReturn = new HashSet<PackProduct>();
        if (ListPackProducts != null) {
            ListPackProducts.stream().forEach(pp -> {
                PackProduct packProduct = new PackProduct();
                if (pp.get("id") != null && !"0".equals(pp.get("id"))) {
                    packProduct.setId(Long.valueOf(pp.get("id").toString()));
                }
                packProduct.setQuantityMin(Integer.valueOf(pp.get("quantityMin").toString()));
                Map<String, Object> p = (Map<String, Object>) pp.get("product");
                Long idProduct = ((Integer) p.get("id")).longValue();
                Product product = new Product();
                product.setId(idProduct);
                packProduct.setProduct(product);
                packProduct.setRules(this.getRules(pp.get("rules")));
                packProductsReturn.add(packProduct);
            });
        }
        return packProductsReturn;
    }


    private Set<Rule> getRules(Object rules) {
        List<Map<String, Object>> ListRules = (ArrayList<Map<String, Object>>) rules;
        Set<Rule> ruleSet = new HashSet<Rule>();
        if (ListRules != null) {
            ListRules.stream().forEach(rule -> {
                Rule r = new Rule();
                Long id = ((Integer) rule.get("id")).longValue();
                r.setId(id);
                ruleSet.add(r);
            });
        }
        return ruleSet;
    }


}
