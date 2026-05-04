package thaumcraft.common.golems.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.seals.SealHandler;

public class TaskHandler {
   static final int TASK_LIMIT = 10000;
   public static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Task>> tasks = new ConcurrentHashMap<>();

   public static void addTask(int dim, Task ticket) {
      if (!tasks.containsKey(dim)) {
         tasks.put(dim, new ConcurrentHashMap<>());
      }

      ConcurrentHashMap<Integer, Task> dc = tasks.get(dim);
      if (dc.size() > 10000) {
         try {
            Iterator<Task> i = dc.values().iterator();
            if (i.hasNext()) {
               i.next();
               i.remove();
            }
         } catch (Exception var4) {
         }
      }

      dc.put(ticket.getId(), ticket);
   }

   public static Task getTask(int dim, int id) {
      return getTasks(dim).get(id);
   }

   public static ConcurrentHashMap<Integer, Task> getTasks(int dim) {
      if (!tasks.containsKey(dim)) {
         tasks.put(dim, new ConcurrentHashMap<>());
      }

      return tasks.get(dim);
   }

   public static ArrayList<Task> getBlockTasksSorted(int dim, UUID uuid, Entity golem) {
      ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
      ArrayList<Task> out = new ArrayList<>();

      label52:
      for (Task ticket : tickets.values()) {
         if (!ticket.isReserved() && ticket.getType() == 0 && (uuid == null || ticket.getGolemUUID() == null || uuid.equals(ticket.getGolemUUID()))) {
            if (out.size() == 0) {
               out.add(ticket);
            } else {
               double d = ticket.getPos().func_177957_d(golem.field_70165_t, golem.field_70163_u, golem.field_70161_v);
               d -= ticket.getPriority() * 256;

               for (int a = 0; a < out.size(); a++) {
                  double d1 = out.get(a).getPos().func_177957_d(golem.field_70165_t, golem.field_70163_u, golem.field_70161_v);
                  d1 -= out.get(a).getPriority() * 256;
                  if (d < d1) {
                     out.add(a, ticket);
                     continue label52;
                  }
               }

               out.add(ticket);
            }
         }
      }

      return out;
   }

   public static ArrayList<Task> getEntityTasksSorted(int dim, UUID uuid, Entity golem) {
      ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
      ArrayList<Task> out = new ArrayList<>();

      label61:
      for (Task ticket : tickets.values()) {
         if (!ticket.isReserved() && ticket.getType() == 1 && (uuid == null || ticket.getGolemUUID() == null || uuid.equals(ticket.getGolemUUID()))) {
            if (ticket.getEntity() == null || ticket.getEntity().field_70128_L) {
               ticket.setSuspended(true);
            } else if (out.size() == 0) {
               out.add(ticket);
            } else {
               double d = ticket.getPos().func_177957_d(golem.field_70165_t, golem.field_70163_u, golem.field_70161_v);
               d -= ticket.getPriority() * 256;

               for (int a = 0; a < out.size(); a++) {
                  double d1 = out.get(a).getPos().func_177957_d(golem.field_70165_t, golem.field_70163_u, golem.field_70161_v);
                  d1 -= out.get(a).getPriority() * 256;
                  if (d < d1) {
                     out.add(a, ticket);
                     continue label61;
                  }
               }

               out.add(ticket);
            }
         }
      }

      return out;
   }

   public static void completeTask(Task task, EntityThaumcraftGolem golem) {
      if (!task.isCompleted() && !task.isSuspended()) {
         ISealEntity se = SealHandler.getSealEntity(golem.field_70170_p.field_73011_w.getDimension(), task.getSealPos());
         if (se != null) {
            task.setCompletion(se.getSeal().onTaskCompletion(golem.field_70170_p, golem, task));
         } else {
            task.setCompletion(true);
         }
      }
   }

   public static void clearSuspendedOrExpiredTasks(World world) {
      ConcurrentHashMap<Integer, Task> tickets = getTasks(world.field_73011_w.getDimension());
      ConcurrentHashMap<Integer, Task> temp = new ConcurrentHashMap<>();

      for (Task ticket : tickets.values()) {
         if (!ticket.isSuspended() && ticket.getLifespan() > 0L) {
            ticket.setLifespan((short)(ticket.getLifespan() - 1L));
            temp.put(ticket.getId(), ticket);
         } else {
            ISealEntity sEnt = SealHandler.getSealEntity(world.field_73011_w.getDimension(), ticket.getSealPos());
            if (sEnt != null) {
               sEnt.getSeal().onTaskSuspension(world, ticket);
            }
         }
      }

      tasks.put(world.field_73011_w.getDimension(), temp);
   }
}
