/**
 * 
 */
package utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * @author Ansen
 *
 */
public final class ResourcePool<R> {

	/** 资源池 **/
	private final LinkedList<R> pool;

	/** 已分配的资源 **/
	private final Set<R> allocated;

	/** 信号灯 **/
	private final Semaphore semaphore;

	/** 资源工厂 **/
	private final IResourceFactory<R> resourceFactory;

	public ResourcePool(final Collection<R> resources) {
		Assert.notNull(resources, "Resources for pool cannot be null");

		this.pool = new LinkedList<R>(resources);
		this.allocated = new HashSet<R>(this.pool.size()+1, 1.0f);
		this.semaphore = new Semaphore(this.pool.size());

		this.resourceFactory = null;
	}

	public ResourcePool(final IResourceFactory<R> resourceFactory, final int poolSize) {
		Assert.notNull(resourceFactory, "Resource factory for pool cannot be null");

		this.resourceFactory = resourceFactory;
		this.pool = new LinkedList<R>();
		for(int i=0; i<poolSize; i++) {
			final R resource = this.resourceFactory.createResource();
			if (resource == null) {
				throw new IllegalStateException(
						"Resource created by factory \"" + this.resourceFactory.getClass().getName() + "\"returned null");
			}
			this.pool.add(resource);
		}

		this.allocated = new HashSet<R>(this.pool.size() + 1, 1.0f);
		this.semaphore = new Semaphore(this.pool.size());
	}

	/**
	 * <p>
	 *   Allocates and returns a resource from the pool.
	 * </p>
	 * 
	 * <p>
	 *   Blocks until a resource is available when a resource is not
	 *   available immediately.
	 * </p>
	 * 
	 * @return the allocated resource, having been removed from the allocation pool.
	 */
	public R allocate() {
		try {
			semaphore.acquire();
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}

		synchronized (this) {
			final R resource = this.pool.removeFirst();
			this.allocated.add(resource);
			return resource;
		}
	}

	/**
	 * <p>
	 *   Releases a previously allocated resource.
	 * </p>
	 * 
	 * <p>
	 *   Might also be used to introduce new resources, e.g. in place of
	 *   a broken resource.
	 * </p>
	 * 
	 * @param resource the resource to be released and returned to the pool.
	 * 
	 */
	public void release(final R resource) {
		synchronized (this) {
			if(allocated.contains(resource)) {
				this.pool.addLast(resource);
				this.allocated.remove(resource);
			}
		}

		this.semaphore.release();
	}

	public void discardAndReplace(final R resource) {
		if (this.resourceFactory == null) {
			throw new IllegalStateException(
					"Cannot execute 'discardAndReplace' operation: no resource " +
					"factory has been set.");
		}

		synchronized(this) {
			if (this.allocated.contains(resource)) {
				final R newResource = this.resourceFactory.createResource();
				this.pool.addLast(newResource);
				this.allocated.remove(resource);
			}
		}

		this.semaphore.release();
	}

	/**
	 * 资源工厂。负责创建资源池中的资源。
	 */
	public static interface IResourceFactory<R> {
		public R createResource();
	}
}
