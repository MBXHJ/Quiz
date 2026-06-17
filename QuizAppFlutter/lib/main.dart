import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'db/database.dart';
import 'models/models.dart';
import 'parsers/txt_parser.dart';
import 'providers/app_provider.dart';
import 'screens/home_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const QuizApp());
}

class QuizApp extends StatelessWidget {
  const QuizApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => AppProvider()..loadBanks(),
      child: MaterialApp(
        title: '刷题助手',
        theme: ThemeData(
          colorSchemeSeed: Colors.blue,
          useMaterial3: true,
        ),
        home: const _InitScreen(),
      ),
    );
  }
}

class _InitScreen extends StatefulWidget {
  const _InitScreen();

  @override
  State<_InitScreen> createState() => _InitScreenState();
}

class _InitScreenState extends State<_InitScreen> {
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _initBuiltinBanks();
  }

  Future<void> _initBuiltinBanks() async {
    final db = AppDatabase();
    if (await db.isDatabaseEmpty()) {
      // Import built-in question banks
      // In Flutter, these would be loaded from assets
    }
    if (mounted) {
      context.read<AppProvider>().loadBanks();
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }
    return const HomeScreen();
  }
}
