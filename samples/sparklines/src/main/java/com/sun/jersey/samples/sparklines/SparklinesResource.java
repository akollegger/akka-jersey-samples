package com.sun.jersey.samples.sparklines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
@Path("/")
@Produces("image/png")
public class SparklinesResource {
    List<Integer> data;

    @DefaultValue("20") @QueryParam("height") int imageHeight;

    Interval limits;

    EntityTag tag;

    public SparklinesResource(
            @QueryParam("d") IntegerList data,
            @DefaultValue("0,100") @QueryParam("limits") Interval limits,
            @Context Request request,
            @Context UriInfo ui) {
        if (data == null)
            throw new WebApplicationException(400);
        
        this.data = data;
        
        this.limits = limits;

        if (!limits.contains(data))
            throw new WebApplicationException(400);

        this.tag = computeEntityTag(ui.getRequestUri());
        if (request.getMethod().equals("GET")) {
            Response.ResponseBuilder rb = request.evaluatePreconditions(tag);
            if (rb != null)
                throw new WebApplicationException(rb.build());
        }
    }

    @Path("discrete")
    @GET
    public Response discrete(
            @DefaultValue("2") @QueryParam("width") int width,
            @DefaultValue("50") @QueryParam("upper") int upper,
            @DefaultValue("red") @QueryParam("upper-color") ColorParam upperColor,
            @DefaultValue("gray") @QueryParam("lower-color") ColorParam lowerColor
            ) {
        BufferedImage image = new BufferedImage(
                data.size() * width - 1, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, image.getWidth(), image.getHeight());

        
        int gap = 4;
        float d = (limits.width() + 1) / (float)(imageHeight - gap);
        for (int i = 0, x = 0, y = 0; i < data.size(); i++, x += width) {
            int v = data.get(i);
            g.setColor((v >= upper) ? upperColor : lowerColor);

            y = imageHeight - (int)((v - limits.lower()) / d);
            g.drawRect(x, y - gap, width - 2, gap);
        }

        return Response.ok(image).tag(tag).build();
    }

    @Path("smooth")
    @GET
    public Response smooth(
            @DefaultValue("2") @QueryParam("step") int step,
            @DefaultValue("true") @QueryParam("min-m") boolean hasMin,
            @DefaultValue("true") @QueryParam("max-m") boolean hasMax,
            @DefaultValue("true") @QueryParam("last-m") boolean hasLast,            
            @DefaultValue("blue") @QueryParam("min-color") ColorParam minColor,
            @DefaultValue("green") @QueryParam("max-color") ColorParam maxColor,
            @DefaultValue("red") @QueryParam("last-color") ColorParam lastColor
            ) {
        BufferedImage image = new BufferedImage(
                data.size() * step - 4, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, image.getWidth(), image.getHeight());

        g.setColor(Color.gray);
        int[] xs = new int[data.size()];
        int[] ys = new int[data.size()];
        int gap = 4;
        float d = (limits.width() + 1) / (float)(imageHeight - gap);
        for (int i = 0, x = 0, y = 0; i < data.size(); i++, x += step) {
            int v = data.get(i);
            xs[i] = x;
            ys[i] = imageHeight - 3 - (int)((v - limits.lower()) / d);
        }
        g.drawPolyline(xs, ys, data.size());

        if (hasMin) {
            int i = data.indexOf(Collections.min(data));
            g.setColor(minColor);
            g.fillRect(xs[i] - step / 2, ys[i] - step / 2, step, step);
        }
        if (hasMax) {
            int i = data.indexOf(Collections.max(data));
            g.setColor(maxColor);
            g.fillRect(xs[i] - step / 2, ys[i] - step / 2, step, step);            
        }
        if (hasMax) {
            g.setColor(lastColor);
            g.fillRect(xs[xs.length - 1] - step / 2, ys[ys.length - 1] - step / 2, step, step);
        }
        
        return Response.ok(image).tag(tag).build();
    }
    
    private EntityTag computeEntityTag(URI u) {
        return new EntityTag(
                computeDigest(u.getRawPath() + u.getRawQuery()));        
    }
    
    private String computeDigest(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] digest = md.digest(content.getBytes());
            BigInteger bi = new BigInteger(digest);
            return bi.toString(16);
        } catch (Exception e) {
            return "";
        }
    }    
}