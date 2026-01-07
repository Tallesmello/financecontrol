package br.com.talles.financecontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.com.talles.financecontrol.ui.add.AddDespesaFragment;
import br.com.talles.financecontrol.ui.categoria.CategoriaFragment;
import br.com.talles.financecontrol.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Fragment inicial (Home)
        carregarFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                carregarFragment(new HomeFragment());
                return true;

            } else if (item.getItemId() == R.id.menu_add) {
                carregarFragment(new AddDespesaFragment());
                return true;

            } else if (item.getItemId() == R.id.menu_categoria) {
                carregarFragment(new CategoriaFragment());
                return true;
            }
            return false;
        });
    }

    private void carregarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerFragment, fragment)
                .commit();
    }
}
