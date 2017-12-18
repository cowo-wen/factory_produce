package com.app.util.tree;

import java.util.List;

public class Node implements java.io.Serializable {
	
    private static final long serialVersionUID = -2721191232926604726L;

    private long id;

    private long parentId;

    private Node parent;

    private List<Node> children;

    private String name;
    
    private String code;

    private int level;

    private int sort;

    private long rootId;

    private int type;

    private boolean isLeaf;

    private String description;
    
    private Object obj;

    public Node() {
        super();
    }

    public Node(int id, int parentId, String name) {
        super();
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }
    
    

    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        long result = 1;
        result = prime * result + id;
        result = prime * result + parentId;
        return (int)result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (id != other.id)
            return false;
        if (parentId != other.parentId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Node {id=" + id + ", parentId=" + parentId + ", children="
                + children + ", name=" + name + ", level =" + level + "}";
    }
}