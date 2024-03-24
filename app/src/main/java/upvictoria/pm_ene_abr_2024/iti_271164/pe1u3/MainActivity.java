package upvictoria.pm_ene_abr_2024.iti_271164.pe1u3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText numCirclesEditText;
    private Button drawButton;
    private Button editSetsButton; // Nuevo botón para editar sets
    private VennDiagramView vennDiagramView;
    private Map<Integer, String> elementsMap = new HashMap<>(); // Mapa para almacenar los elementos de cada conjunto
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numCirclesEditText = findViewById(R.id.num_circles_edit_text);
        drawButton = findViewById(R.id.draw_button);
        editSetsButton = findViewById(R.id.edit_sets_button); // Inicializa el nuevo botón
        saveButton = findViewById(R.id.save_button); // Nuevo botón para guardar
        vennDiagramView = findViewById(R.id.venn_diagram_view);

        // Inicialización de los números de elementos e intersecciones como 0 por defecto
        vennDiagramView.setElementCounts(new int[0]);

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numCirclesStr = numCirclesEditText.getText().toString().trim();
                if (!numCirclesStr.isEmpty()) {
                    int numCircles = Integer.parseInt(numCirclesStr);
                    vennDiagramView.setNumCircles(numCircles);

                    // Colores para los círculos
                    int[] colors = new int[numCircles];
                    for (int i = 0; i < numCircles; i++) {
                        // Generar colores aleatorios
                        colors[i] = Color.rgb((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
                    }
                    vennDiagramView.setColors(colors);
                } else {
                    // Mostrar un mensaje de error o tomar alguna otra acción si el campo está vacío
                    Toast.makeText(MainActivity.this, "Por favor ingresa un número de círculos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el OnClickListener para el botón de editar sets
        editSetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir un diálogo para editar los sets
                openEditSetsDialog();
            }
        });

        // Configurar el OnClickListener para el botón de guardar
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();
            }
        });
    }


    private void openSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar Diagrama");
        final EditText fileNameEditText = new EditText(this);
        fileNameEditText.setHint("Nombre del archivo");
        builder.setView(fileNameEditText);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = fileNameEditText.getText().toString();
                saveDiagram(fileName);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void saveDiagram(String fileName) {
        // Obtener el directorio de almacenamiento externo público
        File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Crear el archivo en el directorio especificado
        File file = new File(externalStorageDir, fileName + ".txt");

        try {
            // Escribir el contenido del diagrama en el archivo
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            // Obtener la información de los conjuntos
            Map<Integer, String> elementsMap = getElementsMap(); // Supongamos que tienes un método para obtener los elementos

            // Escribir cada conjunto y sus elementos en el archivo
            for (Map.Entry<Integer, String> entry : elementsMap.entrySet()) {
                String setName = "Set " + (char)('A' + entry.getKey()); // Nombre del conjunto
                String elements = entry.getValue(); // Elementos asociados al conjunto
                writer.write(setName + ":" + elements);
                writer.newLine(); // Nueva línea para el siguiente conjunto
            }

            // Cerrar el escritor
            writer.close();

            // Mostrar un mensaje de éxito
            Toast.makeText(MainActivity.this, "Diagrama guardado como " + fileName + ".txt", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Manejar cualquier error de E/S que pueda ocurrir
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error al guardar el diagrama", Toast.LENGTH_SHORT).show();
        }
    }

    private Map<Integer, String> getElementsMap() {
        return elementsMap; // Devolver el mapa de elementos que tienes en tu actividad
    }


    private void openEditSetsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Sets");

        // Creamos un layout para el diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Obtener el número de círculos creados en VennDiagramView
        final int numCircles = vennDiagramView.getNumCircles();

        // Obtener los nombres de los sets del VennDiagramView
        final String[] setNames = vennDiagramView.getSetNames();

        // Inicializar arreglo bidimensional para contar elementos en común entre conjuntos
        final int[][] intersectionCounts = new int[numCircles][numCircles];

        // Configurar el diálogo con un campo de texto para cada conjunto creado
        for (int i = 0; i < numCircles; i++) {
            TextView textView = new TextView(this);
            textView.setText(setNames[i]);
            layout.addView(textView);

            EditText editText = new EditText(this);
            editText.setTag(i); // Usamos la etiqueta para identificar qué conjunto se está editando
            layout.addView(editText);

            // Obtener los elementos previamente ingresados para este conjunto, si existen
            String previousElements = elementsMap.get(i);
            if (previousElements != null) {
                editText.setText(previousElements);
            }
        }

        builder.setView(layout);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Actualizar los elementos ingresados en cada conjunto
                int[] elementCounts = new int[numCircles];
                for (int i = 0; i < numCircles; i++) {
                    EditText editText = (EditText) layout.findViewWithTag(i);
                    String editedName = editText.getText().toString().trim();
                    int elementCount = editedName.isEmpty() ? 0 : editedName.split("\\s+").length; // Contar los tokens separados por espacios
                    elementCounts[i] = elementCount;

                    // Guardar los elementos ingresados en este conjunto
                    elementsMap.put(i, editedName);
                }

                // Establecer los recuentos de elementos en VennDiagramView
                vennDiagramView.setElementCounts(elementCounts);

                // Calcular las intersecciones entre conjuntos y mostrar los recuentos en el diagrama
                int[] intersections = new int[numCircles * (numCircles - 1) / 2]; // Array unidimensional para los recuentos de intersección
                int index = 0; // Índice para recorrer el array de intersecciones
                for (int i = 0; i < numCircles; i++) {
                    for (int j = i + 1; j < numCircles; j++) {
                        EditText editTextI = (EditText) layout.findViewWithTag(i);
                        EditText editTextJ = (EditText) layout.findViewWithTag(j);
                        String editedNameI = editTextI.getText().toString().trim();
                        String editedNameJ = editTextJ.getText().toString().trim();

                        // Obtener los elementos comunes entre los conjuntos i y j
                        String[] elementsI = editedNameI.split("\\s+");
                        String[] elementsJ = editedNameJ.split("\\s+");
                        int commonElements = countCommonElements(elementsI, elementsJ);

                        // Asignar el contador de intersección para los conjuntos i y j en el array unidimensional
                        intersections[index] = commonElements;
                        index++;

                        // Actualizar el contador de intersección para los conjuntos i y j
                        intersectionCounts[i][j] = commonElements;
                        intersectionCounts[j][i] = commonElements; // Asegurar simetría en la matriz

                        // Quitar los elementos comunes del contador de elementos si hay alguna intersección
                        if (commonElements > 0) {
                            elementCounts[i] -= commonElements;
                            elementCounts[j] -= commonElements;
                        }
                    }
                }

                // Establecer los recuentos de intersecciones en VennDiagramView
                vennDiagramView.setIntersectionCounts(intersections);



                // Mostrar un mensaje de éxito
                Toast.makeText(MainActivity.this, "Los sets han sido actualizados correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el diálogo
        builder.show();
    }
    private int countCommonElements(String[] arr1, String[] arr2) {
        int count = 0;
        for (String element : arr1) {
            for (String value : arr2) {
                if (element.equalsIgnoreCase(value)) {
                    count++;
                    break; // Contar cada elemento solo una vez
                }
            }
        }
        return count;
    }


}
