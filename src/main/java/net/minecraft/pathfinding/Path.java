package net.minecraft.pathfinding;

import java.util.Objects;

public class Path
{
    /** Contains the points in this path */
    private PathPoint[] pathPoints = new PathPoint[1024];

    /** The number of points in this path */
    private int count;

    /**
     * Adds a point to the path
     */
    public void addPoint(PathPoint point)
    {
        if (point.index >= 0)
        {
            throw new IllegalStateException("OW KNOWS!");
        }
        else
        {
            if (this.count == this.pathPoints.length)
            {
                var apathpoint = new PathPoint[this.count << 1];
                System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
                this.pathPoints = apathpoint;
            }

            this.pathPoints[this.count] = point;
            point.index = this.count;
            this.sortBack(this.count++);
        }
    }

    /**
     * Clears the path
     */
    public void clearPath()
    {
        this.count = 0;
    }

    /**
     * Returns and removes the first point in the path
     */
    public PathPoint dequeue()
    {
        var pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;

        if (this.count > 0)
        {
            this.sortForward(0);
        }

        pathpoint.index = -1;
        return pathpoint;
    }

    /**
     * Changes the provided point's distance to target
     */
    public void changeDistance(PathPoint p_75850_1_, float p_75850_2_)
    {
        var f = p_75850_1_.distanceToTarget;
        p_75850_1_.distanceToTarget = p_75850_2_;

        if (p_75850_2_ < f)
        {
            this.sortBack(p_75850_1_.index);
        }
        else
        {
            this.sortForward(p_75850_1_.index);
        }
    }

    /**
     * Sorts a point to the left
     */
    private void sortBack(int p_75847_1_)
    {
        var pathpoint = this.pathPoints[p_75847_1_];
        var f = pathpoint.distanceToTarget;

        while (p_75847_1_ > 0)
        {
            var i = p_75847_1_ - 1 >> 1;
            var pathpoint1 = this.pathPoints[i];

            if (f >= pathpoint1.distanceToTarget)
            {
                break;
            }

            this.pathPoints[p_75847_1_] = pathpoint1;
            pathpoint1.index = p_75847_1_;
            p_75847_1_ = i;
        }

        this.pathPoints[p_75847_1_] = pathpoint;
        pathpoint.index = p_75847_1_;
    }

    /**
     * Sorts a point to the right
     */
    private void sortForward(int p_75846_1_)
    {
        var pathpoint = this.pathPoints[p_75846_1_];
        var f = pathpoint.distanceToTarget;

        while (true)
        {
            var i = 1 + (p_75846_1_ << 1);
            var j = i + 1;

            if (i >= this.count)
            {
                break;
            }

            var pathpoint1 = this.pathPoints[i];
            var f1 = pathpoint1.distanceToTarget;
            PathPoint pathpoint2;
            float f2;

            if (j >= this.count)
            {
                pathpoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            }
            else
            {
                pathpoint2 = this.pathPoints[j];
                f2 = pathpoint2.distanceToTarget;
            }

            if (f1 < f2)
            {
                if (f1 >= f)
                {
                    break;
                }

                this.pathPoints[p_75846_1_] = pathpoint1;
                pathpoint1.index = p_75846_1_;
                p_75846_1_ = i;
            }
            else
            {
                if (f2 >= f)
                {
                    break;
                }

                this.pathPoints[p_75846_1_] = pathpoint2;
                Objects.requireNonNull(pathpoint2).index = p_75846_1_;
                p_75846_1_ = j;
            }
        }

        this.pathPoints[p_75846_1_] = pathpoint;
        pathpoint.index = p_75846_1_;
    }

    /**
     * Returns true if this path contains no points
     */
    public boolean isPathEmpty()
    {
        return this.count == 0;
    }
}