#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct node {
    char symbol[30];
    int order;
    struct node *left, *right;
} node;

node* newNode(char* symbol)
{
    node* temp = (node*) malloc(sizeof(node));
    strcpy(temp->symbol, symbol);
    temp->order = -1;
    temp->left = temp->right = NULL;
    return temp;
}

node* insert(node* node, char* symbol) {
    if (node == NULL)
        return newNode(symbol);

    int cmp = strcmp(symbol, node->symbol);
    if (cmp < 0)
        node->left = insert(node->left, symbol);
    else if (cmp > 0)
        node->right = insert(node->right, symbol);

    return node;
}

node* get(node* node, char* symbol) {
    if (node == NULL)
        return NULL;

    int cmp = strcmp(symbol, node->symbol);
    if (cmp == 0) {
        return node;
    } else if (cmp < 0) {
        return get(node->left, symbol);
    } else {
        return get(node->right, symbol);
    }
}


//idtable
node* idTable;
int idTableCurrentIndex;

void idTableAdd(char* id) {
    if (idTable == NULL) {
        idTable = insert(idTable, id);
    } else {
        insert(idTable, id);
    }
}

void idTableUpdateOrderRecursive(node* current) {
    if (current != NULL) {
        idTableUpdateOrderRecursive(current->left);
        current->order = idTableCurrentIndex;
        idTableCurrentIndex++;
        idTableUpdateOrderRecursive(current->right);
    }
}

void idTableUpdateOrder()
{
    if (idTable != NULL) {
        idTableCurrentIndex = 0;
        idTableUpdateOrderRecursive(idTable);
    }
}

int idTableGetCode(char* id) {
    node* position = get(idTable, id);

    if (position == NULL)
        return -1;

    return position->order;
}

void idTableFilePrintRecursive(FILE* fptr, node* current) {
    if (current != NULL) {
        idTableFilePrintRecursive(fptr, current->left);
        fprintf(fptr, "%s,%d\n", current->symbol, current->order);
        idTableFilePrintRecursive(fptr, current->right);
    }
}

void idTableFilePrint(FILE* fptr) {
    fprintf(fptr, "ID,CODTS\n");
    idTableFilePrintRecursive(fptr, idTable);
}


//const table
node* constTable;
int constTableCurrentIndex;

void constTableAdd(char* constant) {
    if (constTable == NULL) {
        constTable = insert(constTable, constant);
    } else {
        insert(constTable, constant);
    }
}

void constTableUpdateOrderRecursive(node* current) {
    if (current != NULL) {
        constTableUpdateOrderRecursive(current->left);
        current->order = constTableCurrentIndex;
        constTableCurrentIndex++;
        constTableUpdateOrderRecursive(current->right);
    }
}

void constTableUpdateOrder()
{
    if (constTable != NULL) {
        constTableCurrentIndex = 0;
        constTableUpdateOrderRecursive(constTable);
    }
}

int constTableGetCode(char* constant) {
    node* position = get(constTable, constant);

    if (position == NULL)
        return -1;

    return position->order;
}

void constTableFilePrintRecursive(FILE* fptr, node* current) {
    if (current != NULL) {
        constTableFilePrintRecursive(fptr, current->left);
        fprintf(fptr, "%s,%d\n", current->symbol, current->order);
        constTableFilePrintRecursive(fptr, current->right);
    }
}

void constTableFilePrint(FILE* fptr) {
    fprintf(fptr, "ID,CODTS\n");
    constTableFilePrintRecursive(fptr, constTable);
}