package thaumcraft.codechicken.lib.render;

import java.util.ArrayList;

public class CCRenderPipeline {
   private ArrayList<CCRenderState.VertexAttribute> attribs = new ArrayList<>();
   private ArrayList<CCRenderState.IVertexOperation> ops = new ArrayList<>();
   private ArrayList<CCRenderPipeline.PipelineNode> nodes = new ArrayList<>();
   private ArrayList<CCRenderState.IVertexOperation> sorted = new ArrayList<>();
   private CCRenderPipeline.PipelineNode loading;

   public void setPipeline(CCRenderState.IVertexOperation... ops) {
      this.ops.clear();

      for (int i = 0; i < ops.length; i++) {
         this.ops.add(ops[i]);
      }

      this.rebuild();
   }

   public void reset() {
      this.ops.clear();
      this.unbuild();
   }

   private void unbuild() {
      for (int i = 0; i < this.attribs.size(); i++) {
         this.attribs.get(i).active = false;
      }

      this.attribs.clear();
      this.sorted.clear();
   }

   public void rebuild() {
      if (!this.ops.isEmpty() && CCRenderState.model != null) {
         while (this.nodes.size() < CCRenderState.operationCount()) {
            this.nodes.add(new CCRenderPipeline.PipelineNode());
         }

         this.unbuild();
         if (CCRenderState.useNormals) {
            this.addAttribute(CCRenderState.normalAttrib);
         }

         if (CCRenderState.baseColour != -1 || CCRenderState.alphaOverride >= 0) {
            this.addAttribute(CCRenderState.colourAttrib);
         } else if (CCRenderState.hasColour) {
            CCRenderState.setColour(-1);
         }

         for (int i = 0; i < this.ops.size(); i++) {
            CCRenderState.IVertexOperation op = this.ops.get(i);
            this.loading = this.nodes.get(op.operationID());
            boolean loaded = op.load();
            if (loaded) {
               this.loading.op = op;
            }

            if (op instanceof CCRenderState.VertexAttribute) {
               if (loaded) {
                  this.attribs.add((CCRenderState.VertexAttribute)op);
               } else {
                  ((CCRenderState.VertexAttribute)op).active = false;
               }
            }
         }

         for (int i = 0; i < this.nodes.size(); i++) {
            this.nodes.get(i).add();
         }
      }
   }

   public void addRequirement(int opRef) {
      this.loading.deps.add(this.nodes.get(opRef));
   }

   public void addDependency(CCRenderState.VertexAttribute attrib) {
      this.loading.deps.add(this.nodes.get(attrib.operationID()));
      this.addAttribute(attrib);
   }

   public void addAttribute(CCRenderState.VertexAttribute attrib) {
      if (!attrib.active) {
         this.ops.add(attrib);
         attrib.active = true;
      }
   }

   public void operate() {
      for (int i = 0; i < this.sorted.size(); i++) {
         this.sorted.get(i).operate();
      }
   }

   private class PipelineNode {
      public ArrayList<CCRenderPipeline.PipelineNode> deps = new ArrayList<>();
      public CCRenderState.IVertexOperation op;

      private PipelineNode() {
      }

      public void add() {
         if (this.op != null) {
            for (int i = 0; i < this.deps.size(); i++) {
               this.deps.get(i).add();
            }

            this.deps.clear();
            CCRenderPipeline.this.sorted.add(this.op);
            this.op = null;
         }
      }
   }
}
